package facebook.test.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import facebook.test.model.FacebookUser;

public class FacebookUserDao {

	JdbcTemplate t1;

	public void setT1(JdbcTemplate t1) {
		this.t1 = t1;
	}

	public int saveUserData(FacebookUser user) {
		String sql = "INSERT INTO facebookusers (fbName, fbPassword, email, phoneNumber, gender, nationality) VALUES (?, ?, ?, ?, ?, ?)";
		return t1.update(sql, user.getFbName(), user.getFbPassword(), user.getEmail(), user.getPhoneNumber(),
				user.getGender(), user.getNationality());
	}

	public FacebookUser loginUser(String fbName, String fbPassword) {
		String sql = "SELECT * FROM facebookusers WHERE fbName = ? AND fbPassword = ?";

		// Execute the query
		List<FacebookUser> users = t1.query(sql, new Object[] { fbName, fbPassword }, new RowMapper<FacebookUser>() {
			public FacebookUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				FacebookUser user = new FacebookUser();
				user.setFbId(rs.getInt("fbId"));
				user.setFbName(rs.getString("fbName"));
				user.setFbPassword(rs.getString("fbPassword"));
				user.setEmail(rs.getString("email"));
				user.setPhoneNumber(rs.getString("phoneNumber"));
				user.setGender(rs.getString("gender"));
				user.setNationality(rs.getString("nationality"));
				return user;
			}
		});

		// Check if the list is empty or not and return the user
		return users.isEmpty() ? null : users.get(0);
	}

	public int updatePassword(String fbName, String newPassword) {
		String sql = "UPDATE facebookusers SET fbPassword = ? WHERE fbName = ?";
		return t1.update(sql, newPassword, fbName);
	}

}
