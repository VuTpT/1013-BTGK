/*
 * MiniShop - SPA tĩnh, không cần server ứng dụng.
 *
 * QUAN TRỌNG: mọi quy tắc nghiệp vụ ở đây phải TRÙNG KHỚP với sut-core (Java).
 * Cùng một đặc tả được kiểm thử ở hai mức: unit (Java) và giao diện (Selenium/Playwright).
 */
(function () {
    'use strict';

    // ---------------------------------------------------------------- Hằng số nghiệp vụ
    var MAX_FAILED_ATTEMPTS = 3;
    // Đặc tả là 15 phút; rút xuống 60 giây trên bản web để test UI chạy được trong thời gian
    // chấp nhận được (quyết định thiết kế phục vụ testability - mục 4.4 của đề bài).
    var LOCK_SECONDS = 60;

    var MIN_QUANTITY = 1;
    var MAX_QUANTITY = 10;
    var MAX_DISTINCT_PRODUCTS = 5;

    var VALID_COUPON = 'SALE10';
    var DISCOUNT_THRESHOLD = 500000;

    var NAME_MAX = 50;
    var ADDRESS_MAX = 100;

    var USERS = {
        standard_user: { password: 'secret_sauce', vip: false, disabled: false },
        vip_user:      { password: 'secret_sauce', vip: true,  disabled: false },
        locked_user:   { password: 'secret_sauce', vip: false, disabled: true }
    };

    var PRODUCTS = [
        { code: 'P01', name: 'Áo thun basic',     price: 150000 },
        { code: 'P02', name: 'Quần jean slimfit', price: 350000 },
        { code: 'P03', name: 'Giày sneaker',      price: 500000 },
        { code: 'P04', name: 'Balo laptop',       price: 250000 },
        { code: 'P05', name: 'Nón lưỡi trai',     price: 120000 },
        { code: 'P06', name: 'Kính râm UV400',    price: 200000 }
    ];

    // ---------------------------------------------------------------- Trạng thái
    var state = {
        currentUser: null,
        cart: {},        // { code: quantity }
        coupon: '',
        orderSequence: 0
    };

    function authState() {
        try {
            return JSON.parse(localStorage.getItem('minishop.auth') || '{}');
        } catch (e) {
            return {};
        }
    }

    function saveAuthState(data) {
        localStorage.setItem('minishop.auth', JSON.stringify(data));
    }

    // ---------------------------------------------------------------- FR-01 Đăng nhập
    function login(username, password) {
        if (!username || !username.trim() || !password || !password.trim()) {
            return { status: 'EMPTY_INPUT', message: 'Vui lòng nhập tên đăng nhập và mật khẩu' };
        }

        var account = USERS[username];
        if (!account) {
            return { status: 'INVALID_CREDENTIAL', message: 'Tên đăng nhập hoặc mật khẩu không đúng' };
        }
        if (account.disabled) {
            return { status: 'DISABLED', message: 'Tài khoản đã bị khóa bởi quản trị viên' };
        }

        var all = authState();
        var entry = all[username] || { failed: 0, lockedUntil: 0 };
        var now = Date.now();

        if (entry.lockedUntil && now < entry.lockedUntil) {
            var seconds = Math.ceil((entry.lockedUntil - now) / 1000);
            return { status: 'LOCKED', message: 'Tài khoản đang bị khóa, vui lòng thử lại sau ' + seconds + ' giây' };
        }
        if (entry.lockedUntil) {           // hết hạn khóa -> tự động mở, reset bộ đếm
            entry = { failed: 0, lockedUntil: 0 };
        }

        if (account.password !== password) {
            entry.failed += 1;
            if (entry.failed >= MAX_FAILED_ATTEMPTS) {
                entry.lockedUntil = now + LOCK_SECONDS * 1000;
                all[username] = entry;
                saveAuthState(all);
                return {
                    status: 'LOCKED',
                    message: 'Sai quá ' + MAX_FAILED_ATTEMPTS + ' lần. Tài khoản bị khóa ' + LOCK_SECONDS + ' giây'
                };
            }
            all[username] = entry;
            saveAuthState(all);
            return {
                status: 'INVALID_CREDENTIAL',
                message: 'Tên đăng nhập hoặc mật khẩu không đúng. Còn ' +
                    (MAX_FAILED_ATTEMPTS - entry.failed) + ' lần thử'
            };
        }

        all[username] = { failed: 0, lockedUntil: 0 };
        saveAuthState(all);
        return { status: 'SUCCESS', message: '' };
    }

    // ---------------------------------------------------------------- FR-02 Giỏ hàng
    function productByCode(code) {
        for (var i = 0; i < PRODUCTS.length; i++) {
            if (PRODUCTS[i].code === code) {
                return PRODUCTS[i];
            }
        }
        return null;
    }

    function addToCart(code, quantity) {
        if (isNaN(quantity) || quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            return 'Số lượng phải từ ' + MIN_QUANTITY + ' đến ' + MAX_QUANTITY;
        }
        var current = state.cart[code] || 0;
        if (current === 0 && Object.keys(state.cart).length >= MAX_DISTINCT_PRODUCTS) {
            return 'Giỏ hàng chỉ chứa tối đa ' + MAX_DISTINCT_PRODUCTS + ' loại sản phẩm';
        }
        if (current + quantity > MAX_QUANTITY) {
            return 'Tổng số lượng của một sản phẩm không được vượt quá ' + MAX_QUANTITY;
        }
        state.cart[code] = current + quantity;
        persistCart();
        return null;
    }

    function subtotal() {
        var total = 0;
        Object.keys(state.cart).forEach(function (code) {
            total += productByCode(code).price * state.cart[code];
        });
        return total;
    }

    function cartCount() {
        var count = 0;
        Object.keys(state.cart).forEach(function (code) {
            count += state.cart[code];
        });
        return count;
    }

    function persistCart() {
        if (state.currentUser) {
            localStorage.setItem('minishop.cart.' + state.currentUser, JSON.stringify(state.cart));
        }
    }

    function loadCart() {
        try {
            state.cart = JSON.parse(localStorage.getItem('minishop.cart.' + state.currentUser) || '{}');
        } catch (e) {
            state.cart = {};
        }
    }

    // ---------------------------------------------------------------- FR-03 Khuyến mãi
    function isValidCoupon(coupon) {
        return !!coupon && coupon.trim().toUpperCase() === VALID_COUPON;
    }

    function discountPercent(vip, amount, coupon) {
        var bigOrder = amount >= DISCOUNT_THRESHOLD;
        var validCoupon = isValidCoupon(coupon);
        if (vip && bigOrder && validCoupon) { return 25; }
        if (vip && bigOrder) { return 15; }
        if (vip && validCoupon) { return 20; }
        if (vip) { return 10; }
        if (bigOrder && validCoupon) { return 15; }
        if (bigOrder) { return 5; }
        if (validCoupon) { return 10; }
        return 0;
    }

    function finalAmount() {
        var amount = subtotal();
        var vip = state.currentUser ? USERS[state.currentUser].vip : false;
        var percent = discountPercent(vip, amount, state.coupon);
        return amount - Math.floor(amount * percent / 100);
    }

    // ---------------------------------------------------------------- FR-04 Thanh toán
    function validateCheckout(fullName, phone, address) {
        var errors = {};
        if (!fullName || !fullName.trim()) {
            errors.fullName = 'Họ tên không được để trống';
        } else if (fullName.length > NAME_MAX) {
            errors.fullName = 'Họ tên tối đa ' + NAME_MAX + ' ký tự';
        } else if (!/^[\p{L} ]+$/u.test(fullName)) {
            errors.fullName = 'Họ tên chỉ được chứa chữ cái và khoảng trắng';
        }

        if (!phone || !phone.trim()) {
            errors.phone = 'Số điện thoại không được để trống';
        } else if (!/^0\d{9}$/.test(phone)) {
            errors.phone = 'Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0';
        }

        if (!address || !address.trim()) {
            errors.address = 'Địa chỉ không được để trống';
        } else if (address.length > ADDRESS_MAX) {
            errors.address = 'Địa chỉ tối đa ' + ADDRESS_MAX + ' ký tự';
        }

        if (Object.keys(state.cart).length === 0) {
            errors.cart = 'Giỏ hàng đang trống';
        }
        return errors;
    }

    // ---------------------------------------------------------------- Tiện ích hiển thị
    function $(id) {
        return document.getElementById(id);
    }

    function money(value) {
        return value.toLocaleString('vi-VN') + ' đ';
    }

    function show(element, text) {
        element.textContent = text;
        element.classList.remove('hidden');
    }

    function hide(element) {
        element.textContent = '';
        element.classList.add('hidden');
    }

    function showPage(pageId) {
        ['page-login', 'page-products', 'page-cart', 'page-checkout', 'page-success']
            .forEach(function (id) {
                $(id).classList.toggle('hidden', id !== pageId);
            });
        $('app-header').classList.toggle('hidden', pageId === 'page-login');
    }

    function renderProducts() {
        var grid = $('product-grid');
        grid.innerHTML = '';
        PRODUCTS.forEach(function (product) {
            var card = document.createElement('div');
            card.className = 'product';
            card.setAttribute('data-testid', 'product-' + product.code);
            card.innerHTML =
                '<h3 data-testid="name-' + product.code + '">' + product.name + '</h3>' +
                '<div class="price" data-testid="price-' + product.code + '">' + money(product.price) + '</div>' +
                '<div class="buy">' +
                '  <input type="number" min="1" max="10" value="1" data-testid="qty-' + product.code + '">' +
                '  <button type="button" data-testid="add-' + product.code + '">Thêm vào giỏ</button>' +
                '</div>';
            card.querySelector('button').addEventListener('click', function () {
                var quantity = parseInt(card.querySelector('input').value, 10);
                var error = addToCart(product.code, quantity);
                if (error) {
                    show($('product-message'), error);
                } else {
                    hide($('product-message'));
                }
                refreshCartCount();
            });
            grid.appendChild(card);
        });
    }

    function refreshCartCount() {
        $('cart-count').textContent = String(cartCount());
    }

    function renderCart() {
        var body = $('cart-body');
        body.innerHTML = '';
        var codes = Object.keys(state.cart);

        $('cart-empty').classList.toggle('hidden', codes.length > 0);
        $('cart-table').classList.toggle('hidden', codes.length === 0);

        codes.forEach(function (code) {
            var product = productByCode(code);
            var quantity = state.cart[code];
            var row = document.createElement('tr');
            row.setAttribute('data-testid', 'cart-row-' + code);
            row.innerHTML =
                '<td data-testid="cart-name-' + code + '">' + product.name + '</td>' +
                '<td>' + money(product.price) + '</td>' +
                '<td data-testid="cart-qty-' + code + '">' + quantity + '</td>' +
                '<td data-testid="cart-line-total-' + code + '">' + money(product.price * quantity) + '</td>' +
                '<td><button type="button" class="ghost" data-testid="remove-' + code + '">Xóa</button></td>';
            row.querySelector('button').addEventListener('click', function () {
                delete state.cart[code];
                persistCart();
                renderCart();
                refreshCartCount();
            });
            body.appendChild(row);
        });

        var amount = subtotal();
        var vip = state.currentUser ? USERS[state.currentUser].vip : false;
        $('subtotal').textContent = money(amount);
        $('discount-percent').textContent = discountPercent(vip, amount, state.coupon) + '%';
        $('total').textContent = money(finalAmount());
    }

    // ---------------------------------------------------------------- Gắn sự kiện
    document.addEventListener('DOMContentLoaded', function () {

        $('login-form').addEventListener('submit', function (event) {
            event.preventDefault();
            var result = login($('username').value, $('password').value);
            if (result.status === 'SUCCESS') {
                hide($('login-error'));
                state.currentUser = $('username').value;
                state.coupon = '';
                loadCart();
                $('current-user').textContent = state.currentUser;
                $('password').value = '';
                renderProducts();
                refreshCartCount();
                showPage('page-products');
            } else {
                show($('login-error'), result.message);
            }
        });

        $('logout-button').addEventListener('click', function () {
            state.currentUser = null;
            state.cart = {};
            state.coupon = '';
            $('username').value = '';
            $('password').value = '';
            hide($('login-error'));
            showPage('page-login');
        });

        $('nav-products').addEventListener('click', function (event) {
            event.preventDefault();
            hide($('product-message'));
            showPage('page-products');
        });

        $('nav-cart').addEventListener('click', function (event) {
            event.preventDefault();
            renderCart();
            showPage('page-cart');
        });

        $('apply-coupon').addEventListener('click', function () {
            state.coupon = $('coupon').value;
            if (state.coupon && !isValidCoupon(state.coupon)) {
                show($('cart-message'), 'Mã giảm giá không hợp lệ');
            } else {
                hide($('cart-message'));
            }
            renderCart();
        });

        $('clear-cart').addEventListener('click', function () {
            state.cart = {};
            state.coupon = '';
            $('coupon').value = '';
            persistCart();
            renderCart();
            refreshCartCount();
        });

        $('go-checkout').addEventListener('click', function () {
            ['fullName', 'phone', 'address', 'cart'].forEach(function (field) {
                hide($('error-' + field));
            });
            showPage('page-checkout');
        });

        $('checkout-form').addEventListener('submit', function (event) {
            event.preventDefault();
            var errors = validateCheckout($('fullName').value, $('phone').value, $('address').value);

            ['fullName', 'phone', 'address', 'cart'].forEach(function (field) {
                if (errors[field]) {
                    show($('error-' + field), errors[field]);
                } else {
                    hide($('error-' + field));
                }
            });
            if (Object.keys(errors).length > 0) {
                return;
            }

            var total = finalAmount();
            state.orderSequence += 1;
            $('order-id').textContent = 'MS' + String(state.orderSequence).padStart(4, '0');
            $('order-total').textContent = money(total);

            state.cart = {};
            state.coupon = '';
            persistCart();
            refreshCartCount();
            showPage('page-success');
        });

        $('back-to-products').addEventListener('click', function () {
            showPage('page-products');
        });

        showPage('page-login');
    });

    // Cho phép test tự động dọn trạng thái khóa tài khoản giữa các test case.
    window.MiniShopTestHooks = {
        resetAuthState: function () {
            localStorage.removeItem('minishop.auth');
        },
        lockSeconds: LOCK_SECONDS
    };
})();
