package com.lmd.pmas.contact;

public class ContactModel implements Cloneable{
	private int _id;
	private int gr_id;
	private String name;
	private String index_name;
	private int birthday;
	private String phone;
	private String email;
	private String address;
	
    public ContactModel clone(){
    	ContactModel o = null;
        try{
            o = (ContactModel)super.clone();
        }
        catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;
    }
    
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getGr_id() {
		return gr_id;
	}
	public void setGr_id(int gr_id) {
		this.gr_id = gr_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIndex_name() {
		return index_name;
	}
	public void setIndex_name(String index_name) {
		this.index_name = index_name;
	}
	public int getBirthday() {
		return birthday;
	}
	public void setBirthday(int birthday) {
		this.birthday = birthday;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
