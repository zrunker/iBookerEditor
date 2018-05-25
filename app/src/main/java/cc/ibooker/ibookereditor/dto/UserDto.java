package cc.ibooker.ibookereditor.dto;

import cc.ibooker.ibookereditor.bean.UserEntity;

/**
 * 保存用户相关信息
 * 
 * @author 邹峰立
 */
public class UserDto {
	private UserEntity user;
	private String ua;
	private String token;

	public UserDto() {
		super();
	}

	public UserDto(UserEntity user, String ua, String token) {
		super();
		this.user = user;
		this.ua = ua;
		this.token = token;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "UserDto [user=" + user + ", ua=" + ua + ", token=" + token
				+ "]";
	}

}
