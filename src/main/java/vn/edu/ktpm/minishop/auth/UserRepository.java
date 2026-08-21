package vn.edu.ktpm.minishop.auth;

/**
 * Cong ra ben ngoai cua {@link LoginService}.
 *
 * <p>Duoc tach thanh interface de tang unit test co the <b>gia lap (mock)</b> bang EasyMock,
 * khong can co so du lieu that - dung yeu cau cua bai W10.</p>
 */
public interface UserRepository {

    /** @return tai khoan tuong ung, hoac {@code null} neu khong ton tai. */
    User findByUsername(String username);

    /** Luu lai trang thai tai khoan (so lan sai, thoi diem het khoa). */
    void save(User user);
}
