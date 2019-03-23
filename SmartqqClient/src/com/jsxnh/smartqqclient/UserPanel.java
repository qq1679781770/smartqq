/**
 * 
 * author@jsxnh
 */

package com.jsxnh.smartqqclient;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import com.jsxnh.component.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class UserPanel extends JFrame{

	/*
	 * 信息标识符
	 * */
	public final static Integer UpdateNicknameMessage=0,UpdateSignatureMessage=1,UpdateDatasMessage=2,
			                    DeletePacketMessage=3,AddPacketMessage=4,ModifyPacketMessage=5,
			                    ModifyRemarkMessage=6,MovePacketMessage=7,AddFriendMessage=8,
			                    AddFriendResultMessage=9,AgreeAddFriendMessage=10,DisagreeFriendMessage=11,
			                    FindUserByIdSuccessMessage=12,FindUserByNicknameSuccessMessage=13,
			                    FindUserByIdFailureMessage=14,FindUserByNicknameFailureMessage=15,
			                    ChatMessage=16,LoginAddFriend=17,LoginMessages=18,SendFileResult=19,
								GetFiles=20,ReceiveFileMResult=21,FileFeedBack=22,LoginFileFeedBack=23;
	
	private User user;
	private Map<String, LinkedList<Friend>> friends=new HashMap<>();

	public Map<Integer, String> getMessages() {
		return messages;
	}

	private Map<Integer, String> messages=new HashMap<Integer, String>();//消息map
	private Map<Integer, ChatPanel> chatpanels=new HashMap<>();
	private JTree friendsTree;//朋友列表树
	private FriendNode root;
	private JScrollPane jsPanel;
	private Container con=this.getContentPane();
	private JLabel nickname,signature,weather;
	private JLabel headimg;
	private JLabel headimglb;
	private JLabel MSGLb;

	public JLabel getFileManager() {
		return FileManager;
	}

	private JLabel FileManager;
	private Point point;
	private JButton close,min,searchbt,MSGBt,FileManagerb;
	private JTextField searchtf;
	private JMenuBar modify;
	private Friend currentfriend;
	private String currentpacket;
	private FriendNode currentnode;
	private JPopupMenu popupmenu;
	private Integer x,y;
	
	public JLabel getMsgLb(){
		return this.MSGLb;
	}
	
	public void injectMessage(Integer message,String json){
		messages.put(message, json);
	}
	
	public Map<Integer, ChatPanel> getchatpanels(){
		return this.chatpanels;
	}
	
	public User getUser(){
		return this.user;
	}
	
	public UserPanel(){
		
	}
	
	/*
	 * 初始化用户和朋友信息
	 * */
	private void initUserandFriend(String str){
		JSONObject json=JSONObject.fromObject(str);
		JSONObject user=json.getJSONObject("user");
		this.user=new User();
		this.user.setUser_id((Integer)user.get("user_id"));
		this.user.setNickname(user.getString("nickname"));
		if(user.containsKey("signature")){
			this.user.setSignature(user.getString("signature"));
		}
		if(user.containsKey("name")){
			this.user.setName(user.getString("name"));
		}
		if(user.containsKey("age")){
			this.user.setAge(user.getInt("age"));
		}
		if(user.containsKey("message")){
			this.user.setMessage(user.getString("message"));
		}		
		this.user.setStatus(user.getInt("status"));
		JSONArray packets=user.getJSONArray("packets");
		for(int i=0;i<packets.size();i++){
			this.user.getPackets().add(packets.getString(i));
		}
		JSONArray friends=json.getJSONArray("friends");
		for(int i=0;i<friends.size();i++){
			JSONObject subpacket=friends.getJSONObject(i);
			Iterator it=subpacket.keys();
			String packetname=it.next().toString();
			JSONArray subfriends=subpacket.getJSONArray(packetname);
			LinkedList<Friend> friendslist=new LinkedList<>();
			for(int j=0;j<subfriends.size();j++){
				Friend friend=new Friend();
				friend.setNickname(subfriends.getJSONObject(j).getString("nickname"));
				friend.setUser_id(subfriends.getJSONObject(j).getInt("user_id"));
				if(subfriends.getJSONObject(j).containsKey("remarkname")){
					friend.setRemarkname(subfriends.getJSONObject(j).getString("remarkname"));
				}
				if(subfriends.getJSONObject(j).containsKey("signature")){
					friend.setSignature(subfriends.getJSONObject(j).getString("signature"));
				}				
				friend.setStatus(subfriends.getJSONObject(j).getInt("status"));
				friend.setPacket(packetname);
				friendslist.add(friend);
			}
			this.friends.put(packetname, friendslist);
		}

	}
	
	public void initRoot(){
		//初始化朋友树
		root=new FriendNode(new ImageIcon("images/headimg.png"),"root","root");
		for(Map.Entry<String, LinkedList<Friend>> entry:friends.entrySet()){
			FriendNode packetname=new FriendNode(new ImageIcon("images/arrow_right.png"),entry.getKey()+"("+entry.getValue().size()+")",entry.getKey());
			for(Friend friend_:entry.getValue()){
				packetname.add(new FriendNode(new ImageIcon("images/headimg.png"),friend_.toString(),friend_.getSignature(),friend_));
			}
			root.add(packetname);
		}
	}
	
	public void lunch(String str){
		initUserandFriend(str);
		this.setLayout(null);
		this.setSize(338, 758);
		/*
		 * 可移动
		 * */
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e){
				point=e.getPoint();
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e){
				Point newpoint=e.getLocationOnScreen();
				setLocation(newpoint.x-point.x, newpoint.y-point.y);
			}
		});
		
		//昵称
		nickname=new JLabel();
		nickname.setText(user.getNickname());
		nickname.setFont(new Font("微软雅黑",Font.BOLD,12));
		nickname.setForeground(Color.black);
		nickname.setBounds(79, 37, 80, 17);
		con.add(nickname);
		//头像
		headimg=new JLabel(new ImageIcon("images\\headimg2.jpg"));
		headimglb=new JLabel(new ImageIcon("images\\headimgbg.png"));
		headimg.setBounds(11, 41, 61, 60);
		headimglb.setBounds(9, 39, 65, 65);
		con.add(headimg);
		con.add(headimglb);
		headimg.addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				headimglb.setBorder(BorderFactory.createLineBorder(new Color(147,112,219),20));
			}
			public void mouseEntered(MouseEvent e) {
				headimglb.setBorder(BorderFactory.createLineBorder(new Color(199,21,133 ),20));
				}
		});
		//个性签名
		signature=new JLabel();
		signature.setText(user.getSignature());
		signature.setFont(new Font("微软雅黑",Font.PLAIN,12));
		signature.setForeground(Color.black);
		signature.setBounds(79, 54, 200, 20);
		con.add(signature);
		weather=new  JLabel();
		weather.setIcon(new ImageIcon("images\\tianqi.png"));
		weather.setBounds(220, 30, 60, 50);
		con.add(weather);
		
		searchtf=new JTextField("搜索账号，昵称");
		searchtf.setBorder(null);
		searchtf.setFont(new Font("楷体",Font.PLAIN,14));
		searchtf.setForeground(Color.darkGray);
		searchtf.setBackground(new Color(248,248,255));
        searchtf.addMouseListener(new MouseAdapter() {	
			public void mouseEntered(MouseEvent e) {				
				searchtf.setBackground(Color.white);
				}
		});
        searchtf.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				searchtf.setText("");   
			}
			@Override
			public void mouseExited(MouseEvent e) {
				//searchtf.setText("搜索账号，昵称");
			}
		});
        searchbt=new JButton();
        searchbt.setIcon(new ImageIcon("images\\search.png"));
        searchbt.setRolloverIcon(new ImageIcon("images\\search_hover.png"));
        searchbt.setPressedIcon(new ImageIcon("images\\search_press.png"));
        searchbt.setBorder(null);
        searchbt.setFocusPainted(false);
        searchbt.setContentAreaFilled(false);
        searchtf.setBounds(1, 108, 249, 33);
        searchbt.setBounds(250, 111, 22, 25);
        searchbt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String str=searchtf.getText();
				if(str.equals("")){
					return;
				}
				if(str.matches("\\d+")){
					SendtoServer.finduserById(Integer.parseInt(str));
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(FindUserByIdFailureMessage)){
						JOptionPane.showMessageDialog(UserPanel.this, "无账号");
						messages.remove(FindUserByIdFailureMessage);
					}else{						
						new addFriendbyidFrame().lunch(messages.get(FindUserByIdSuccessMessage));
						messages.remove(FindUserByIdSuccessMessage);
					}
				}else{
					SendtoServer.finduserByName(str);
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(FindUserByNicknameFailureMessage)){
						JOptionPane.showMessageDialog(UserPanel.this, "无账号");
						messages.remove(FindUserByNicknameFailureMessage);
					}else{
						new addFriendbynicknameFrame().lunch(messages.get(FindUserByNicknameSuccessMessage));
						messages.remove(FindUserByNicknameSuccessMessage);
					}					
				}
			}
		});
        con.add(searchtf);
        con.add(searchbt);
        
		jsPanel=new JScrollPane();
		jsPanel.setBorder(null);
		jsPanel.setBounds(2, 182, 278, 430);
		jsPanel.setVisible(true);		
		initRoot();
		DefaultTreeModel model=new DefaultTreeModel(root);
		friendsTree=new JTree();
		friendsTree.setVisible(true);
		friendsTree.setModel(model);
		friendsTree.setRootVisible(false);
		friendsTree.setCellRenderer(new CellRenderer());
		friendsTree.setUI(new FriendTreeUI());
		//获取点击分组对象或朋友对象
		friendsTree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				FriendNode node=(FriendNode) friendsTree.getLastSelectedPathComponent();
				TreePath path = friendsTree.getSelectionPath();
				if(path != null) {
					if(friendsTree.isCollapsed(path)){
						friendsTree.expandPath(path);
					}else{
						friendsTree.collapsePath(path);
					}
					// 展开节点
				}

				// TODO Auto-generated method stub

				if(node==null){
					return;
				}
				currentnode=node;
				if(node.getUserObject()==null){
					return;
				}
				if(node.getUserObject() instanceof Friend){
					currentfriend=(Friend)node.getUserObject();
					currentpacket = null;
				}else{
					currentpacket=(String)node.getUserObject();
					currentfriend = null;
				}
				friendsTree.clearSelection();

			}
		});
		jsPanel.setViewportView(friendsTree);
		friendsTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e){
				if(e.isPopupTrigger()){
					x = e.getX();
					y = e.getY();
					popupmenu.show(friendsTree, e.getX(), e.getY());
				}
			}
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()){
					x = e.getX();
					y = e.getY();
					popupmenu.show(friendsTree, e.getX(), e.getY());
				}
		   }
		});
		con.add(jsPanel);
		//右击菜单
		popupmenu=new JPopupMenu();
		popupmenu.setVisible(true);
		JMenuItem deletepacket=new JMenuItem("删除分组");
		deletepacket.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(currentpacket==null||currentnode.equals("")){
					JOptionPane.showMessageDialog(UserPanel.this, "请选择删除的分组");
					return;
				}
				if(friends.get(currentpacket).size()>0){
					JOptionPane.showMessageDialog(UserPanel.this, "请删除空的分组");
					currentpacket = null;
					return;
				}
				SendtoServer.deletePacket(user.getUser_id(), currentpacket);
				synchronized (UserPanel.this) {
					try {
						UserPanel.this.wait();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(messages.containsKey(DeletePacketMessage)){
					messages.remove(DeletePacketMessage);
					user.getPackets().remove(currentpacket);
					friends.remove(currentpacket);
					JOptionPane.showMessageDialog(UserPanel.this, "删除成功");
					root.remove(currentnode);
					friendsTree.setModel(new DefaultTreeModel(root));
					jsPanel.repaint();
					currentpacket = null;
					currentnode = null;
				}
			}
		});
		JMenuItem addpacket=new JMenuItem("增加分组");
		addpacket.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new addpacketFrame().lunch();
			}
		});
		JMenuItem modifypacket=new JMenuItem("修改分组");
		//修改分组监听事件
		modifypacket.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new modifypacketFrame().lunch();
			}
			
		});
		JMenuItem movepacket=new JMenuItem("移动分组");
		movepacket.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
                if(currentfriend==null){
                    JOptionPane.showMessageDialog(UserPanel.this, "选择要移动的好友");
                    return;
                }
				new movepacketFrame().lunch();
			}
		});
		JMenuItem modifyremark=new JMenuItem("修改备注");
		modifyremark.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
                if(currentfriend==null){
                    JOptionPane.showMessageDialog(UserPanel.this, "选择好友");
                    return;
                }
				new modifyremarkFrame().lunch();
			}
		});
		JMenuItem friendmessage=new JMenuItem("查看资料");
		friendmessage.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
                if(currentfriend==null){
                    JOptionPane.showMessageDialog(UserPanel.this, "选择好友");
                    return;
                }
				new friendmessageFrame();
			}
		});
		JMenuItem chatwith=new JMenuItem("发送消息");
		chatwith.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
                if(currentfriend==null){
                    JOptionPane.showMessageDialog(UserPanel.this, "选择好友");
                    return;
                }
				ChatPanel chatpanel=new ChatPanel();
				chatpanels.put(currentfriend.getUser_id(), chatpanel);
				chatpanel.lunch(UserPanel.this, currentfriend, "");
			}
		});
		JMenuItem shownickname=new JMenuItem("显示昵称");
		shownickname.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				for(Map.Entry<String, LinkedList<Friend>> entry:friends.entrySet()){
					LinkedList<Friend> friends_=entry.getValue();
					for(Friend item:friends_){
						item.setIsnickname(true);
					}
				}
				initRoot();
				friendsTree.setModel(new DefaultTreeModel(root));
				jsPanel.setViewportView(friendsTree);
				jsPanel.repaint();
				jsPanel.validate();
			}
		});
		JMenuItem showremark=new JMenuItem("显示备注");
		showremark.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				for(Map.Entry<String, LinkedList<Friend>> entry:friends.entrySet()){
					LinkedList<Friend> friends_=entry.getValue();
					for(Friend item:friends_){
						item.setIsnickname(false);
					}
				}
				initRoot();
				friendsTree.setModel(new DefaultTreeModel(root));
				jsPanel.setViewportView(friendsTree);
				jsPanel.repaint();
				jsPanel.validate();
			}
		});
		JMenuItem sendfile = new JMenuItem("发送文件");
		sendfile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentfriend==null){
					JOptionPane.showMessageDialog(UserPanel.this, "选择好友");
					return;
				}
				JFileChooser jfc=new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
				jfc.setMultiSelectionEnabled(false);
				jfc.showOpenDialog(UserPanel.this);
				File file = jfc.getSelectedFile();
				if(file!=null){
					SendtoServer.sendFile(user.getUser_id(),currentfriend.getUser_id(),file.getName(),file);
					synchronized (UserPanel.this){
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(SendFileResult)){
						messages.remove(SendFileResult);
						JOptionPane.showMessageDialog(UserPanel.this, "发送成功");
					}
				}


				currentnode = null;
				currentfriend = null;
				currentpacket = null;
			}
		});
		popupmenu.add(deletepacket);
		popupmenu.add(addpacket);
		popupmenu.add(modifypacket);
		popupmenu.add(movepacket);
		popupmenu.addSeparator();
		popupmenu.add(modifyremark);
		popupmenu.add(friendmessage);
		popupmenu.add(chatwith);
		popupmenu.add(sendfile);
		popupmenu.addSeparator();
		popupmenu.add(shownickname);
		popupmenu.add(showremark);
		popupmenu.setInvoker(friendsTree);
		con.add(popupmenu);
		
		
		modify=new JMenuBar();		
		JMenu edit=new JMenu("编辑资料");
		JMenuItem editnickname=new JMenuItem("修改昵称");
		JMenuItem editsignature=new JMenuItem("修改签名");
		JMenuItem editdatas=new JMenuItem("修改资料");
		editnickname.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new nicknameFrame().lunch();
			}
		});
		editsignature.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new signatureFrame().lunch();
			}
		});
		editdatas.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new datasFrame().lunch();
			}
		});
		edit.add(editnickname);
		edit.add(editsignature);
		edit.add(editdatas);
		modify.add(edit);
		modify.setBounds(207, 0, 80, 20);
		//modify.setBorder(null);
		con.add(modify);
		
		MSGLb=new JLabel();
		MSGLb.setFont(new Font("宋体",Font.PLAIN,14));
		MSGLb.setForeground(Color.white);
		MSGLb.setText("无消息");
		MSGLb.setBounds(147, 0, 60, 18);
		MSGBt=new JButton();
		MSGBt.setBorder(null);
		MSGBt.setFocusPainted(false);
		MSGBt.setContentAreaFilled(false);
		MSGBt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(messages.containsKey(AddFriendMessage)){
					new acceptaddfriendFrame().lunch(messages.get(AddFriendMessage));
					messages.remove(AddFriendMessage);
				}
				if(messages.containsKey(AgreeAddFriendMessage)){
					if(!messages.get(AgreeAddFriendMessage).equals("添加成功")){
						JSONObject addfriend=JSONObject.fromObject(messages.get(AgreeAddFriendMessage)).getJSONObject("agreeaddfriend");
						Integer user_id=addfriend.getInt("user2_id");
						JOptionPane.showMessageDialog(UserPanel.this, String.valueOf(user_id)+"同意添加");
						Friend newFriend=new Friend();
						newFriend.setUser_id(user_id);
						newFriend.setNickname(addfriend.getString("nickname2"));
						if(addfriend.containsKey("signature2")){
							newFriend.setSignature(addfriend.getString("signature2"));
						}						
						newFriend.setStatus(addfriend.getInt("status2"));
						newFriend.setPacket(addfriend.getString("packet1_name"));
						friends.get(newFriend.getPacket()).add(newFriend);
						initRoot();
						friendsTree.setModel(new DefaultTreeModel(root));
						jsPanel.setViewportView(friendsTree);
						jsPanel.repaint();
						jsPanel.validate();
						messages.remove(AgreeAddFriendMessage);
					}
				}
				if(messages.containsKey(DisagreeFriendMessage)){
					if(!messages.get(DisagreeFriendMessage).equals("拒绝成功")){
						JSONObject disagreejson=JSONObject.fromObject(messages.get(DisagreeFriendMessage)).getJSONObject("disagreeaddfriend");
						Integer user_id=disagreejson.getInt("user2_id");
						JOptionPane.showMessageDialog(UserPanel.this, String.valueOf(user_id)+"拒绝添加");
						messages.remove(DisagreeFriendMessage);
					}
				}
				if(messages.containsKey(ChatMessage)){
					JSONObject message=JSONObject.fromObject(messages.get(ChatMessage));					
					ChatPanel chatpanel=new ChatPanel();
					chatpanels.put(message.getInt("user1_id"), chatpanel);
					chatpanel.lunch(UserPanel.this, null, message.toString());			
					messages.remove(ChatMessage);
				}
				if(messages.containsKey(LoginAddFriend)){
					new acceptaddfriendFrame2().lunch(messages.get(LoginAddFriend));
					messages.remove(LoginAddFriend);
				}
				if(messages.containsKey(LoginMessages)){
					JSONArray mess = JSONArray.fromObject(messages.get(LoginMessages));
					for(int i=0;i<mess.size();i++){
						JSONObject j = mess.getJSONObject(i);
						ChatPanel chatpanel=new ChatPanel();
						chatpanels.put(j.getInt("user1_id"), chatpanel);
						Friend f = new Friend();
						f.setUser_id(j.getInt("user1_id"));
						f.setNickname(j.getString("nickname1"));
						if(j.containsKey("signature1")){
							f.setSignature(j.getString("signature1"));
						}
						f.setStatus(j.getInt("status1"));
						chatpanel.lunch(UserPanel.this,f,"");
						JSONArray contents = j.getJSONArray("contents");
						for(int ii=0;ii<contents.size();ii++){
							JSONObject message = contents.getJSONObject(ii);
							SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							SendtoServer.receiveChat(message.getInt("user1_id"), message.getInt("user2_id"), message.getString("content"),
									message.getString("send_time"), df.format(new Date()));
							chatpanel.appendMessage(message);
						}
					}
					messages.remove(LoginMessages);
				}
				if(messages.containsKey(FileFeedBack)){

					JSONObject json = JSONObject.fromObject(messages.get(FileFeedBack));
					messages.remove(FileFeedBack);
					JOptionPane.showMessageDialog(UserPanel.this, json.getString("username")+"已接收文件"+json.getString("filename"));

				}
				if(messages.containsKey(LoginFileFeedBack)){
					JSONArray jsonArray = JSONArray.fromObject(messages.get(LoginFileFeedBack));
					messages.remove(LoginFileFeedBack);
					String s = "";
					for(int i=0;i<jsonArray.size();i++){
						s = s+jsonArray.getJSONObject(i).getString("username")+"已接收文件 "+jsonArray.getJSONObject(i).getString("filename")+"\n";
					}
					JOptionPane.showMessageDialog(UserPanel.this, s);
				}
				MSGLb.setText("无消息");
			}
		});
		MSGBt.setBounds(147, 0, 60, 18);
		con.add(MSGLb);
		con.add(MSGBt);
		FileManager = new JLabel();
		FileManager.setFont(new Font("宋体",Font.PLAIN,14));
		FileManager.setForeground(Color.WHITE);
		FileManager.setText("文件管理");
		FileManager.setBounds(67,0,80,18);
		FileManagerb = new JButton();
		FileManagerb.setBorder(null);
		FileManagerb.setFocusPainted(false);
		FileManagerb.setContentAreaFilled(false);
		FileManagerb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SendtoServer.getFiles(user.getUser_id());
				synchronized (UserPanel.this){
					try {
						UserPanel.this.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				if(messages.containsKey(GetFiles)){
					new fileFrame().lunch(messages.get(GetFiles));
					messages.remove(GetFiles);
				}
				FileManager.setText("文件管理");
			}
		});
		FileManagerb.setBounds(67,0,80,18);
		con.add(FileManager);
		con.add(FileManagerb);
		/**
		close=new JButton();
		close.setIcon(new ImageIcon("images\\Mainclose.png"));
		close.setRolloverIcon(new ImageIcon("images\\Mainclose_hover.png"));
		close.setPressedIcon(new ImageIcon("images\\Mainclose_press.png"));
		close.setBorder(null);
		close.setFocusPainted(false);
		close.setContentAreaFilled(false);
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SendtoServer.logout(user.getUser_id());
			    UserPanel.this.dispose();				
			}
		});
		min=new JButton();
		min.setIcon(new ImageIcon("images\\Mainmin.png"));
		min.setRolloverIcon(new ImageIcon("images\\Mainmin_hover.png"));
		min.setPressedIcon(new ImageIcon("images\\Mainmin_press.png"));
		min.setBorder(null);
		min.setToolTipText("最小化");
		min.setFocusPainted(false);
		min.setContentAreaFilled(false);
		min.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setExtendedState(JFrame.ICONIFIED);
			}
		});
		min.setBounds(227,-2 ,28, 20);
		close.setBounds(255, -2, 28, 20);
		con.add(close);
		con.add(min);**/
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				SendtoServer.logout(user.getUser_id());
                try {
                    SendtoServer.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				SendtoServer.logout(user.getUser_id());
                try {
                    SendtoServer.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
			}
		});
		backgournd bg=new backgournd();
		bg.setImage(this.getToolkit().getImage("images\\mainbg.png"));
		bg.setBounds(0, 0, 284, 674);
		con.add(bg);
		this.setIconImage(this.getToolkit().getImage("images\\title.png"));
		this.setUndecorated(true);
//		this.setAlwaysOnTop(true);
		this.setVisible(true);
		JSONObject json = JSONObject.fromObject(str);
		if(json.containsKey("addFriends")){
			getMsgLb().setText("有消息");
			injectMessage(LoginAddFriend,json.getJSONArray("addFriends").toString());
		}
		if(json.containsKey("chatMessages")){
			getMsgLb().setText("有消息");
			injectMessage(LoginMessages,json.getJSONArray("chatMessages").toString());
		}
		if(json.containsKey("filereceive")){
			getFileManager().setText("待收文件");
		}
		if(json.containsKey("filefeedback")){
			getMsgLb().setText("有消息");
			injectMessage(LoginFileFeedBack,json.getJSONArray("filefeedback").toString());
		}
	}
	
	public void notifyall(Integer messageid,String str){
		synchronized (this) {
			this.messages.put(messageid, str);
			notifyAll();
		}
	}
	
	/*
	 * 修改昵称面板
	 */
	private class nicknameFrame extends JFrame{
		private JTextField subnickname;
		private JButton confirm;
		private Container container=this.getContentPane();
		public nicknameFrame(){
			
		}		
		public void lunch(){
			this.setLayout(null);
			this.setSize(360, 150);
			subnickname=new JTextField(1000);
			//subnickname.setBorder(BorderFactory.createLineBorder(Color.blue));
            subnickname.setText(user.getNickname());
			confirm=new JButton("确认修改");
			subnickname.setBounds(20, 20, 174, 28);
			confirm.setBounds(200, 20, 86, 28);
			confirm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String text=subnickname.getText();
					if(text.equals("")){
						JOptionPane.showMessageDialog(nicknameFrame.this, "昵称不能为空");
						return;
					}
					SendtoServer.updateNickname(user.getUser_id(), text);
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}					
					if(messages.containsKey(UpdateNicknameMessage)){
					    nicknameFrame.this.dispose();
						user.setNickname(text);
						nickname.setText(text);
						JOptionPane.showMessageDialog(nicknameFrame.this, "修改成功");
						messages.remove(UpdateNicknameMessage);
					}
				}
			});
			container.add(subnickname);
			container.add(confirm);
            background bg=new background();
            bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
            bg.setBounds(0, 0,360, 150);
            container.add(bg);
            container.repaint();
			this.setVisible(true);
		}
	}
	
	/**
	 * 修改个性签名面板
	 *
	 */
	private class signatureFrame extends JFrame{
		private JTextField subsignature;
		private JButton confirm;
		private Container container=this.getContentPane();
		public signatureFrame(){
			
		}		
		public void lunch(){
			this.setLayout(null);
			this.setSize(360, 150);
			subsignature=new JTextField(1000);
			//subsignature.setBorder(BorderFactory.createLineBorder(Color.blue));
            subsignature.setText(user.getSignature());
			confirm=new JButton("确认修改");
			subsignature.setBounds(20, 20, 174, 28);
			confirm.setBounds(200, 20, 86, 28);
			confirm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String text=subsignature.getText();
					SendtoServer.updateSignature(user.getUser_id(), text);
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}					
					if(messages.containsKey(UpdateSignatureMessage)){
					    signatureFrame.this.dispose();
						user.setSignature(text);
						signature.setText(text);
						JOptionPane.showMessageDialog(signatureFrame.this, "修改成功");
						messages.remove(UpdateSignatureMessage);
					}
				}
			});
			container.add(subsignature);
			container.add(confirm);
            background bg=new background();
            bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
            bg.setBounds(0, 0,360, 150);
            container.add(bg);
            container.repaint();
			this.setVisible(true);
		}
	}
	
	/*
	 * 修改信息面板
	 * */
	
	private class datasFrame extends JFrame{
		private JLabel namelabel,agelabel,messagelabel;
		private JTextField nametf,agetf;
		private JTextArea messagetf;
		private JButton confirm;
		private Container container=this.getContentPane();
		public datasFrame(){
			
		}
		public void lunch(){
			setLayout(null);
			setSize(380, 300);
			Font font=new Font("楷体",Font.BOLD,19);
			namelabel=new JLabel("真实姓名");
			namelabel.setFont(font);
			nametf=new JTextField(1000);
			if(user.getName()!=null){
				nametf.setText(user.getName());
			}
			//nametf.setBorder(BorderFactory.createLineBorder(Color.blue));
			namelabel.setBounds(20, 20, 86, 28);
			nametf.setBounds(110, 20, 174, 28);
			container.add(namelabel);
			container.add(nametf);
			agelabel=new JLabel("年    龄");
			agelabel.setFont(font);
			agetf=new JTextField(1000);
			if(user.getAge()!=null){
				agetf.setText(String.valueOf(user.getAge()));
			}
			//agetf.setBorder(BorderFactory.createLineBorder(Color.blue));
			agelabel.setBounds(20, 53, 86, 28);
			agetf.setBounds(110, 53, 174, 28);
			container.add(agelabel);
			container.add(agetf);
			messagelabel=new JLabel("备注信息");
			messagelabel.setFont(font);
			messagetf=new JTextArea();
			messagetf.setLineWrap(true);
			if(user.getMessage()!=null){
				messagetf.setText(user.getMessage());
			}
			messagelabel.setBounds(20, 86, 86, 28);
			messagetf.setBounds(110, 86, 174, 84);
			container.add(messagelabel);
			container.add(messagetf);
			confirm=new JButton("确认修改");
			confirm.setBounds(120, 180, 100, 30);
			confirm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String namestr=nametf.getText();
					String agestr=agetf.getText();
					String messagestr=messagetf.getText();
					if(!agestr.matches("\\d+")){
					    JOptionPane.showMessageDialog(datasFrame.this, "年龄为数字");
					    return;
					}
					SendtoServer.updateDatas(user.getUser_id(), namestr, Integer.parseInt(agestr), messagestr);
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(UpdateDatasMessage)){
					    datasFrame.this.dispose();
						user.setName(namestr);
						user.setAge(Integer.parseInt(agestr));
						user.setMessage(messagestr);
						messages.remove(UpdateDatasMessage);
						JOptionPane.showMessageDialog(datasFrame.this, "修改成功");
					}
				}
			});
			container.add(confirm);
            background bg=new background();
            bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
            bg.setBounds(0, 0,380, 300);
            container.add(bg);
            container.repaint();
			this.setVisible(true);
		}
	}
	
	private class addpacketFrame extends JFrame{	
		private JTextField addpackettf;
		private JButton confirm;
		private Container container=this.getContentPane();
		public void lunch(){
			this.setLayout(null);
			this.setSize(360, 150);
			container.setBackground(new Color(180,188,192));
			this.setTitle("添加分组");
			addpackettf=new JTextField(1000);
			//addpackettf.setBorder(BorderFactory.createLineBorder(Color.blue));
			addpackettf.setBounds(20, 20, 124, 28);
			confirm=new JButton("确认添加");
			confirm.setBounds(181, 20, 86, 28);
			confirm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String str=addpackettf.getText();
					if(str.equals("")){
						JOptionPane.showMessageDialog(UserPanel.this, "请输入分组");
						return;
					}
					if(friends.containsKey(str)){
						JOptionPane.showMessageDialog(UserPanel.this, "请输入不同分组");
						return;
					}
					SendtoServer.addPacket(user.getUser_id(), str);
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(AddPacketMessage)){
						addpacketFrame.this.dispose();
						JOptionPane.showMessageDialog(UserPanel.this, "添加成功");
						messages.remove(AddPacketMessage);
						friends.put(str, new LinkedList<Friend>());
						user.getPackets().add(str);
						root.add(new FriendNode(new ImageIcon("images/arrow_right.png"),str+"(0)",str));
						friendsTree.setModel(new DefaultTreeModel(root));
						jsPanel.setViewportView(friendsTree);
						jsPanel.repaint();
						jsPanel.validate();
						
					}
				}
			});
			container.add(addpackettf);
			container.add(confirm);
			background bg=new background();
			bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
			bg.setBounds(0, 0,360, 150);
			container.add(bg);
			container.repaint();
			if(x!=null) {
				this.setLocation(x, y);
			}
			this.setVisible(true);
		}
	}
	
	private class modifypacketFrame extends JFrame{
		private JTextField packettf;
		private JButton confirm;
		private Container container=this.getContentPane();
		public void lunch(){
			this.setLayout(null);
			this.setSize(360, 150);
			this.setTitle("修改分组");
			packettf=new JTextField(1000);
			//packettf.setBorder(BorderFactory.createLineBorder(Color.blue));
			packettf.setBounds(20, 20, 124, 28);
			packettf.setText(currentpacket);
			confirm=new JButton("确认修改");
			confirm.setBounds(181, 20, 86, 28);
			confirm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if(currentpacket==null){
						JOptionPane.showMessageDialog(UserPanel.this, "请选择分组");
						return;
					}
					String str=packettf.getText();
					if(packettf.getText().equals("")){
						JOptionPane.showMessageDialog(UserPanel.this, "不为空");
						return;
					}
					SendtoServer.modifyPacket(user.getUser_id(), currentpacket, packettf.getText());
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(ModifyPacketMessage)){
						modifypacketFrame.this.dispose();
						JOptionPane.showMessageDialog(UserPanel.this, "修改成功");
						messages.remove(ModifyPacketMessage);
						user.getPackets().remove(currentpacket);
						user.getPackets().add(str);
						LinkedList<Friend> friends_=friends.get(currentpacket);
						friends.remove(currentpacket);
						friends.put(str, friends_);
						initRoot();
						friendsTree.setModel(new DefaultTreeModel(root));
						jsPanel.setViewportView(friendsTree);
						jsPanel.repaint();
						jsPanel.validate();
						currentnode = null;
						currentpacket = null;
					}
				}
			});
			container.add(packettf);
			container.add(confirm);
			background bg=new background();
			bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
			bg.setBounds(0, 0,360, 150);
			container.add(bg);
			container.repaint();
			setVisible(true);
		}
	}
	
	private class friendmessageFrame extends JFrame{
		private JLabel nicknamelb,nicknametextlb,user_idlb,user_idtextlb,
		               signaturelb,signaturetextlb,statuslb,statustextlb,
		               remarklb,remarktextlb;
		private Container container=this.getContentPane();
		public friendmessageFrame(){
			setSize(430, 350);
			setLayout(null);
			Font font=new Font("楷体",Font.BOLD,19);
			nicknamelb=new JLabel("昵    称");
			nicknamelb.setFont(font);
			nicknametextlb=new JLabel();
			nicknametextlb.setFont(font);
			nicknametextlb.setText(currentfriend.getNickname());
			nicknamelb.setBounds(50, 50, 86, 28);
			nicknametextlb.setBounds(160, 50, 200, 28);
			container.add(nicknamelb);
			container.add(nicknametextlb);
			user_idlb=new JLabel("账    号");
			user_idlb.setFont(font);
			user_idtextlb=new JLabel();
			user_idtextlb.setFont(font);
			user_idtextlb.setText(String.valueOf(currentfriend.getUser_id()));
			user_idlb.setBounds(50, 88, 86, 28);
			user_idtextlb.setBounds(160, 88, 200, 28);
			container.add(user_idlb);
			container.add(user_idtextlb);
			signaturelb=new JLabel("个性签名");
			signaturelb.setFont(font);
			signaturetextlb=new JLabel();
			signaturetextlb.setFont(font);

			signaturelb.setBounds(50, 121, 86, 28);
			signaturetextlb.setBounds(160, 121, 200, 56);
            if(currentfriend.getSignature()!=null){
                try {
                    JlabelSetText(signaturetextlb,currentfriend.getSignature());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
			container.add(signaturelb);
			container.add(signaturetextlb);
			statuslb=new JLabel("状    态");
			statuslb.setFont(font);
			statustextlb=new JLabel();
			statustextlb.setFont(font);
			if(currentfriend.getStatus().equals(1)){
				statustextlb.setText("在线");
			}else{
				statustextlb.setText("离线");
			}
			statuslb.setBounds(50, 187, 86, 28);
			statustextlb.setBounds(160, 187, 200, 28);
			remarklb=new JLabel("备    注");
			remarklb.setFont(font);
			remarktextlb=new JLabel();
			remarktextlb.setFont(font);
			if(currentfriend.getRemarkname()!=null){
				remarktextlb.setText(currentfriend.getRemarkname());
			}
			remarklb.setBounds(50, 220, 86, 28);
			remarktextlb.setBounds(160, 220,200, 28);
			container.add(statuslb);
			container.add(statustextlb);
			container.add(remarklb);
			container.add(remarktextlb);
            background bg=new background();
            bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
            bg.setBounds(0, 0,430, 350);
            container.add(bg);
            container.repaint();
			setVisible(true);
			
		}

        void JlabelSetText(JLabel jLabel, String longString)
                throws InterruptedException {
            StringBuilder builder = new StringBuilder("<html>");
            char[] chars = longString.toCharArray();
            FontMetrics fontMetrics = jLabel.getFontMetrics(jLabel.getFont());
            int start = 0;
            int len = 0;
            while (start + len < longString.length()) {
                while (true) {
                    len++;
                    if (start + len > longString.length())break;
                    if (fontMetrics.charsWidth(chars, start, len)
                            > jLabel.getWidth()) {
                        break;
                    }
                }
                builder.append(chars, start, len-1).append("<br/>");
                start = start + len - 1;
                len = 0;
            }
            builder.append(chars, start, longString.length()-start);
            builder.append("</html>");
            jLabel.setText(builder.toString());
        }
	}
	
	private class modifyremarkFrame extends JFrame{
		private JTextField remarktf;
		private JButton confirm;
		public void lunch(){
			this.setSize(320, 180);
			this.setLayout(null);
			this.setTitle("修改备注");
			remarktf=new JTextField(1000);
			//remarktf.setBorder(BorderFactory.createLineBorder(Color.black));
			if(currentfriend.getRemarkname()!=null){
				remarktf.setText(currentfriend.getRemarkname());
			}
			confirm=new JButton("确认修改");
			remarktf.setBounds(20, 20, 174, 28);
			confirm.setBounds(200, 20, 86, 28);
			confirm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String str=remarktf.getText();
					if(remarktf.getText().equals("")){
						JOptionPane.showMessageDialog(modifyremarkFrame.this, "请填写备注");
						return;
					}
					SendtoServer.modifyRemark(user.getUser_id(), currentfriend.getUser_id(),str );
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(ModifyRemarkMessage)){
					    modifyremarkFrame.this.dispose();
						messages.remove(ModifyRemarkMessage);
						JOptionPane.showMessageDialog(UserPanel.this, "修改成功");
						LinkedList<Friend> friends_=friends.get(currentfriend.getPacket());
						for(Friend item:friends_){
							if(item.equals(currentfriend)){
								item.setRemarkname(str);
							}
						}
						initRoot();
						friendsTree.setModel(new DefaultTreeModel(root));
						jsPanel.setViewportView(friendsTree);
						jsPanel.repaint();
						jsPanel.validate();
					}
				}
			});
			this.getContentPane().add(remarktf);
			this.getContentPane().add(confirm);
            background bg=new background();
            bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
            bg.setBounds(0, 0,320, 180);
            this.getContentPane().add(bg);
            this.getContentPane().repaint();
			this.setVisible(true);
		}
	}
	
	private class movepacketFrame extends JFrame{
		private JComboBox<String> packetbox;
		private JButton confirm;
		private Container container=this.getContentPane();
		public void lunch(){
			setSize(300, 150);
			setLayout(null);
			setTitle("移动分组");
			String[] packets=user.getPackets().toArray(new String[0]);
			packetbox=new JComboBox<>(packets);
			packetbox.setBounds(20, 20, 86, 28);
			confirm=new JButton("确认移动");
			confirm.setBounds(120, 20, 86, 28);
			confirm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub

					String packet_=(String) packetbox.getSelectedItem();
					String oldpacket=currentfriend.getPacket();
					SendtoServer.movePacket(user.getUser_id(), currentfriend.getUser_id(), currentfriend.getPacket(), packet_);
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(MovePacketMessage)){
					    movepacketFrame.this.dispose();
						messages.remove(MovePacketMessage);
						JOptionPane.showMessageDialog(UserPanel.this, "移动成功");
						friends.get(oldpacket).remove(currentfriend);
						currentfriend.setPacket(packet_);
						friends.get(packet_).add(currentfriend);
						initRoot();
						friendsTree.setModel(new DefaultTreeModel(root));
						jsPanel.setViewportView(friendsTree);
						jsPanel.repaint();
						jsPanel.validate();
						currentfriend = null;
						currentnode = null;
					}
				}
			});
			container.add(packetbox);
			container.add(confirm);
            background bg=new background();
            bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
            bg.setBounds(0, 0,300, 150);
            container.add(bg);
            container.repaint();
			setVisible(true);
		}
	}
	
	private class addFriendbyidFrame extends JFrame{
		private JLabel user_idlb,user_idtextlb,nicknamelb,nicknametextlb;
		private JButton add;
		private Container container=this.getContentPane();
		public void lunch(String json){
			JSONObject userjson=JSONObject.fromObject(json);
			Integer user_id=userjson.getInt("user_id");
			String nickanme=userjson.getString("nickname");
			setLayout(null);
			setSize(360, 300);
			Font font=new Font("楷体",Font.BOLD,19);
			user_idlb=new JLabel("账    号:");
			user_idlb.setFont(font);
			user_idtextlb=new JLabel();
			user_idtextlb.setFont(font);
			user_idtextlb.setText(String.valueOf(user_id));
			user_idlb.setBounds(40, 40, 100, 28);
			user_idtextlb.setBounds(150, 40, 150, 28);
			container.add(user_idlb);
			container.add(user_idtextlb);
			nicknamelb=new JLabel("昵    称:");
			nicknamelb.setFont(font);
			nicknametextlb=new JLabel();
			nicknametextlb.setFont(font);
			nicknametextlb.setText(nickanme);
			nicknamelb.setBounds(40, 73, 100, 28);
			nicknametextlb.setBounds(150, 73, 150, 28);
			container.add(nicknamelb);
			container.add(nicknametextlb);
			add=new JButton("确认添加");
			add.setBounds(80, 120, 120, 28);
			add.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					addFriendbyidFrame.this.dispose();
					new addFriendFrame().lunch(user_id);
				}
			});
			container.add(add);
			background bg=new background();
			bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
			bg.setBounds(0, 0,360, 300);
			container.add(bg);
			container.repaint();
			setVisible(true);
		}
	}
	
	private class addFriendbynicknameFrame extends JFrame{
		private JLabel[] user_idtextlb;
		private JLabel[] nicknametextlb;
		private JButton[] confirm;
		private JLabel user_idlb,nicknamelb;
		private Container container=this.getContentPane();
		public void lunch(String json){
			JSONArray usersjson=JSONArray.fromObject(json);
			Integer size=usersjson.size();
			setSize(560, 33*size+200);
			setLayout(null);
			Font font=new Font("楷体",Font.BOLD,19);
			
			user_idlb=new JLabel("账      号");
			user_idlb.setFont(font);
			nicknamelb=new JLabel("昵      称");
			nicknamelb.setFont(font);
			user_idlb.setBounds(40, 40, 150, 28);
			nicknamelb.setBounds(200, 40, 150, 28);
			container.add(user_idlb);
			container.add(nicknamelb);
			user_idtextlb=new JLabel[size];
			nicknametextlb=new JLabel[size];
			confirm=new JButton[size];
			for(int i=0;i<size;i++){
				JSONObject item=usersjson.getJSONObject(i);
				user_idtextlb[i]=new JLabel();
				user_idtextlb[i].setText(String.valueOf(item.getInt("user_id")));
				user_idtextlb[i].setFont(font);
				user_idtextlb[i].setBounds(40, 40+33*(i+1), 150,28);
				nicknametextlb[i]=new JLabel();
				nicknametextlb[i].setText(item.getString("nickname"));
				nicknametextlb[i].setFont(font);
				nicknametextlb[i].setBounds(200, 40+33*(i+1), 150, 28);
				confirm[i]=new JButton("确认添加");
				confirm[i].setBounds(370, 40+33*(i+1), 120, 28);
				confirm[i].addActionListener(new MyactionListener());
				container.add(user_idtextlb[i]);
				container.add(nicknametextlb[i]);
				container.add(confirm[i]);
			}
			background bg=new background();
			bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
			bg.setBounds(0, 0,560, 33*size+200);
			container.add(bg);
			container.repaint();
			setVisible(true);
		}
		
		public class MyactionListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				for(int i=0;i<confirm.length;i++){
					if(e.getSource()==confirm[i]){
						new addFriendFrame().lunch(Integer.parseInt(user_idtextlb[i].getText()));
						break;
					}
				}
			}			
		}
	}
	
	private class addFriendFrame extends JFrame{
		private JComboBox<String> packetbox;
		private JButton confirm;
		private Container container=this.getContentPane();
		public void lunch(Integer user_id){
			setSize(320, 150);
			setLayout(null);
			setTitle("添加好友");
			String[] packets=user.getPackets().toArray(new String[0]);
			packetbox=new JComboBox<>(packets);
			packetbox.setBounds(20, 20, 86, 28);
			confirm=new JButton("确认添加");
			confirm.setBounds(126, 20, 86, 28);
			confirm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String packet_=(String) packetbox.getSelectedItem();
					SendtoServer.addFriend(user.getUser_id(), user_id, packet_);
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(AddFriendResultMessage)){
						addFriendFrame.this.dispose();
						JOptionPane.showMessageDialog(UserPanel.this, messages.get(AddFriendResultMessage));
						messages.remove(AddFriendResultMessage);
					}
				}
			});
			container.add(packetbox);
			container.add(confirm);
			background bg=new background();
			bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
			bg.setBounds(0, 0,320, 150);
			container.add(bg);
			container.repaint();
			setVisible(true);
		}
	}
	
	private class acceptaddfriendFrame extends JFrame{
		private JLabel user_idlb,user_idtextlb;
		private JButton agree,disagree;
		private Container container=this.getContentPane();
		
		public void lunch(String json){
			JSONObject jobject=JSONObject.fromObject(json);
			Integer  user_id=jobject.getInt("user1_id");
			setLayout(null);
			setSize(320, 220);
			setTitle("添加好友");
			Font font=new Font("楷体",Font.BOLD,19);
			user_idlb=new JLabel("账  号");
			user_idlb.setFont(font);
			user_idtextlb=new JLabel();
			user_idtextlb.setFont(font);
			user_idtextlb.setText(String.valueOf(user_id));
			user_idlb.setBounds(20, 20, 86, 28);
			user_idtextlb.setBounds(110, 20, 174, 28);
			agree=new JButton("同意添加");
			agree.setBounds(20, 60, 80, 28);
			agree.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
                    acceptaddfriendFrame.this.dispose();
					new agreeaddfriendFrame().lunch(json);
				}
			});
			disagree=new JButton("拒绝添加");
			disagree.setBounds(160, 60, 80, 28);
			disagree.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
                    acceptaddfriendFrame.this.dispose();
					SendtoServer.disagreeAddFriend(user_id,jobject.getInt("user2_id"));
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(DisagreeFriendMessage)){
						if(messages.get(DisagreeFriendMessage).equals("拒绝成功")){
							JOptionPane.showMessageDialog(UserPanel.this, "拒绝成功");
							messages.remove(DisagreeFriendMessage);
						}
					}
				}
			});
			container.add(user_idlb);
			container.add(user_idtextlb);
			container.add(agree);
			container.add(disagree);
			background bg=new background();
			bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
			bg.setBounds(0, 0,320, 220);
			container.add(bg);
			container.repaint();
			setVisible(true);
		}
	}

	private class acceptaddfriendFrame2 extends JFrame{
		private JLabel[] user_idlb;
		private JLabel user_lb,operator_lb;
		private JButton[] agree;

		public JButton[] getAgree() {
			return agree;
		}

		public void setAgree(JButton[] agree) {
			this.agree = agree;
		}

		public JButton[] getDisagree() {
			return disagree;
		}

		public void setDisagree(JButton[] disagree) {
			this.disagree = disagree;
		}

		private JButton[] disagree;
		private Container container=this.getContentPane();
		private JSONArray array;
		public void lunch(String json){
			JSONArray jsonarray = JSONArray.fromObject(json);
			array = jsonarray;
			Integer size=jsonarray.size();
			setSize(550, 33*size+200);
			setLayout(null);
			Font font=new Font("楷体",Font.BOLD,19);
			user_lb = new JLabel("账号");
			user_lb.setFont(font);
			operator_lb = new JLabel("操作");
			operator_lb.setFont(font);
			user_lb.setBounds(40,40,150,28);
			operator_lb.setBounds(210,40,250,28);
			container.add(user_lb);
			container.add(operator_lb);
			user_idlb = new JLabel[size];
			agree = new JButton[size];
			disagree = new JButton[size];
			for(int i =0;i<size;i++){
				JSONObject item = jsonarray.getJSONObject(i);
				user_idlb[i] = new JLabel();
				user_idlb[i].setText(String.valueOf(item.getInt("user1_id")));
				user_idlb[i].setFont(font);
				user_idlb[i].setBounds(40,73+33*i,150,28);
				agree[i] = new JButton("确认添加");
				agree[i].setBounds(210,73+33*i,120,28);
				disagree[i] = new JButton("拒绝添加");
				disagree[i].setBounds(340,73+33*i,120,28);
				agree[i].addActionListener(new agreeActionListener());
				disagree[i].addActionListener(new disagreeActionListener());
				container.add(user_idlb[i]);
				container.add(agree[i]);
				container.add(disagree[i]);
			}
			background bg=new background();
			bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
			bg.setBounds(0, 0,550, 33*size+200);
			container.add(bg);
			container.repaint();
			setVisible(true);
		}

		public class agreeActionListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<agree.length;i++){
					if(e.getSource()==agree[i]){
						JSONObject jobject = array.getJSONObject(i);
						new agreeaddfriendFrame2().lunch(jobject.toString(),i);
						break;
					}
				}
			}
		}

		public class disagreeActionListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<disagree.length;i++){
					if(e.getSource()==disagree[i]){
						JSONObject jobject = array.getJSONObject(i);
						SendtoServer.disagreeAddFriend(jobject.getInt("user1_id"),jobject.getInt("user2_id"));
						synchronized (UserPanel.this) {
							try {
								UserPanel.this.wait();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						if(messages.containsKey(DisagreeFriendMessage)){
							if(messages.get(DisagreeFriendMessage).equals("拒绝成功")){
								JOptionPane.showMessageDialog(UserPanel.this, "拒绝成功");
								messages.remove(DisagreeFriendMessage);
								disagree[i].setText("已拒绝");
								disagree[i].setEnabled(false);
								agree[i].setEnabled(false);
							}
						}
						break;
					}
				}
			}
		}

		public class agreeaddfriendFrame2 extends JFrame{
			private JComboBox<String> packetbox;
			private JButton confirm;
			private Container container=this.getContentPane();
			public void lunch(String  json,int index){
				JSONObject userjson=JSONObject.fromObject(json);
				setSize(320, 150);
				setLayout(null);
				setTitle("添加好友");
				String[] packets=user.getPackets().toArray(new String[0]);
				packetbox=new JComboBox<>(packets);
				packetbox.setBounds(20, 20, 86, 28);
				confirm=new JButton("确认添加");
				confirm.setBounds(120, 20, 86, 28);
				confirm.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String packet_=(String) packetbox.getSelectedItem();
						SendtoServer.agreeAddFriend(userjson.getInt("user1_id"),userjson.getInt("user2_id"),userjson.getString("packet1_name"),
								packet_);
						synchronized (UserPanel.this) {
							try {
								UserPanel.this.wait();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						if(messages.containsKey(AgreeAddFriendMessage)){
							if(messages.get(AgreeAddFriendMessage).equals("添加成功")){
								agreeaddfriendFrame2.this.dispose();
								acceptaddfriendFrame2.this.getAgree()[index].setEnabled(false);
								acceptaddfriendFrame2.this.getAgree()[index].setText("已同意");
								acceptaddfriendFrame2.this.getDisagree()[index].setEnabled(false);
								messages.remove(AgreeAddFriendMessage);
								JOptionPane.showMessageDialog(UserPanel.this, "添加成功");
								Friend friend_=new Friend();
								friend_.setUser_id(userjson.getInt("user1_id"));
								friend_.setNickname(userjson.getString("nickname1"));
								friend_.setStatus(userjson.getInt("status1"));
								friend_.setPacket(packet_);
								if(userjson.containsKey("signature1")){
									friend_.setSignature(userjson.getString("signature1"));
								}
								friends.get(friend_.getPacket()).add(friend_);
								initRoot();
								friendsTree.setModel(new DefaultTreeModel(root));
								jsPanel.setViewportView(friendsTree);
								jsPanel.repaint();
								jsPanel.validate();

							}
						}
					}
				});
				container.add(packetbox);
				container.add(confirm);
				background bg=new background();
				bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
				bg.setBounds(0, 0,320, 150);
				container.add(bg);
				container.repaint();
				setVisible(true);
			}
		}

	}
	
	private class agreeaddfriendFrame extends JFrame{
		private JComboBox<String> packetbox;
		private JButton confirm;
		private Container container=this.getContentPane();
		public void lunch(String  json){
			JSONObject userjson=JSONObject.fromObject(json);
			setSize(320, 150);
			setLayout(null);
			setTitle("添加好友");
			String[] packets=user.getPackets().toArray(new String[0]);
			packetbox=new JComboBox<>(packets);
			packetbox.setBounds(20, 20, 86, 28);
			confirm=new JButton("确认添加");
			confirm.setBounds(120, 20, 86, 28);
			confirm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String packet_=(String) packetbox.getSelectedItem();
					SendtoServer.agreeAddFriend(userjson.getInt("user1_id"),userjson.getInt("user2_id"),userjson.getString("packetname"),
							                packet_);
					synchronized (UserPanel.this) {
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(AgreeAddFriendMessage)){
						if(messages.get(AgreeAddFriendMessage).equals("添加成功")){
						    agreeaddfriendFrame.this.dispose();
							messages.remove(AgreeAddFriendMessage);
							JOptionPane.showMessageDialog(UserPanel.this, "添加成功");
							Friend friend_=new Friend();
							friend_.setUser_id(userjson.getInt("user1_id"));
							friend_.setNickname(userjson.getString("nickname1"));
							friend_.setStatus(userjson.getInt("status1"));
							friend_.setPacket(packet_);
							if(userjson.containsKey("signature1")){
								friend_.setSignature(userjson.getString("signature1"));
							}
							friends.get(friend_.getPacket()).add(friend_);
							initRoot();
							friendsTree.setModel(new DefaultTreeModel(root));
							jsPanel.setViewportView(friendsTree);
							jsPanel.repaint();
							jsPanel.validate();							
						}
					}
				}
			});
			container.add(packetbox);
			container.add(confirm);
			background bg=new background();
			bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
			bg.setBounds(0, 0,320, 150);
			container.add(bg);
			container.repaint();
			setVisible(true);
		}
	}

	private class fileFrame extends JFrame{

		private JTabbedPane jTabbedPane;
		private JPanel panel1,panel2,panel3;
		private JScrollPane jsp1,jsp2,jsp3;
		private JList list1,list2,list3;
		private Container container = this.getContentPane();
		private ArrayList<JSONObject> l = new ArrayList<>();
		public fileFrame(){

		}

		public void lunch(String json){
			JSONObject jobject = JSONObject.fromObject(json);
			setLayout(null);
			setSize(858,710);
			jTabbedPane = new JTabbedPane();
			panel1 = new JPanel();
			panel1.setLayout(null);
			panel2 = new JPanel();
			panel2.setLayout(null);
			panel3 = new JPanel();
			panel3.setLayout(null);
			DefaultListModel model1 = new DefaultListModel();
			JSONArray json1 = jobject.getJSONArray("hassend");
			for(int i=0;i<json1.size();i++){
				model1.addElement(json1.getJSONObject(i));
			}
			list1 = new JList();
			list1.setModel(model1);
			list1.setFixedCellHeight(36);
			list1.setCellRenderer(new ListCellRenderer1());
			jsp1 = new JScrollPane();
			jsp1.setViewportView(list1);
			jsp1.setBounds(0,0,800,600);
			panel1.add(jsp1);

			DefaultListModel model2 = new DefaultListModel();
			JSONArray json2 = jobject.getJSONArray("receive");
			for(int i=0;i<json2.size();i++){
				model2.addElement(json2.getJSONObject(i));
				l.add(json2.getJSONObject(i));
			}
			list2 = new JList();
			list2.setModel(model2);
			list2.setFixedCellHeight(36);
			list2.setCellRenderer(new ListCellRenderer2(UserPanel.this));
			list2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
			list2.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					Object object = list2.getSelectedValue();
					JSONObject j = (JSONObject)object;
					if(j==null){
						return;
					}
					System.out.println(j);

					SendtoServer.Receive(j.getInt("id"));
					synchronized (UserPanel.this){
						try {
							UserPanel.this.wait();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					if(messages.containsKey(ReceiveFileMResult)){
						JOptionPane.showMessageDialog(UserPanel.this, "接收成功");
						messages.remove(ReceiveFileMResult);
						for(JSONObject i:l){
							if(i.getInt("id")==j.getInt("id")){
								i.put("status",2);
								break;
							}
						}
						DefaultListModel model = new DefaultListModel();
						for(JSONObject i:l){
							model.addElement(i);
						}
						list2.setModel(model);
						jsp2.setViewportView(list2);
						jsp2.repaint();
						jsp2.validate();

					}
					list2.clearSelection();
				}
			});
			jsp2 = new JScrollPane();
			jsp2.setViewportView(list2);
			jsp2.setBounds(0,0,800,600);
			panel2.add(jsp2);

			DefaultListModel model3 = new DefaultListModel();
			JSONArray json3 = jobject.getJSONArray("hasreceive");
			for(int i=0;i<json3.size();i++){
				model3.addElement(json3.getJSONObject(i));
			}
			list3 = new JList();
			list3.setModel(model3);
			list3.setFixedCellHeight(36);
			list3.setCellRenderer(new ListCellRenderer3());
			jsp3 = new JScrollPane();
			jsp3.setViewportView(list3);
			jsp3.setBounds(0,0,800,600);
			panel3.add(jsp3);
			jTabbedPane.add("已发送",panel1);
			jTabbedPane.add("待接收",panel2);
			jTabbedPane.add("已接收",panel3);
			setContentPane(jTabbedPane);
			background bg=new background();
			bg.setImage(this.getToolkit().getImage("images\\bg1.jpg"));
			bg.setBounds(0, 0,900, 700);
			container.add(bg);
			container.repaint();

			setVisible(true);
		}

	}

	private class backgournd extends JPanel{
		private Image image;
		public backgournd(){
			setOpaque(false);
			setLayout(null);
		}
		public void setImage(Image image){
			this.image=image;
		}
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			if(image!=null){
				g.drawImage(image, 0, 0,this);
			}
		}
	}	
}
