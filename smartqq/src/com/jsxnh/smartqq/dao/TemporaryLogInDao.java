package com.jsxnh.smartqq.dao;
import com.jsxnh.smartqq.entities.TemporaryLogIn;

public interface TemporaryLogInDao {

	//�����ʱ��¼��¼
	public void  addTemporaryLogIn(TemporaryLogIn temporylogin);
	//�ǳ�ʱɾ����ʱ��¼��¼
    public void deleteTemporaryLogIn(Integer user_id);
    //�����û�����ʱ��¼
    public TemporaryLogIn findlogIn(Integer user_id);
}
