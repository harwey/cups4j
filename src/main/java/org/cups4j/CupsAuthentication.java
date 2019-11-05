package org.cups4j;

public class CupsAuthentication {
	
	private final String userid;
	
	private final String password;
	
	public CupsAuthentication(String userid, String password) {
		super();
		this.userid = userid;
		this.password = password;
	}

	public String getUserid() {
		return userid;
	}

	public String getPassword() {
		return password;
	}

}
