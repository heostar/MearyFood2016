package kr.sc.mearyfood2012;
import java.io.*;
import java.net.*;
import java.util.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class MearyFoodWeekly extends Activity {
	
	
	ArrayList<Menus> menusList;
	MenuAdapter m_adapter;
	ProgressDialog mProgress;
	DownThread wThread;
	Calendar cal = new GregorianCalendar();
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weekly);
						
		mProgress = ProgressDialog.show(MearyFoodWeekly.this, "��ٷ��ּ���~!", "�ٿ�ε����Դϴ�...\n(�ִ�ҿ�ð� 10��)");
		wThread = new DownThread();
		wThread.start();
	}
	
	private class MenuAdapter extends ArrayAdapter<Menus> {
		
		public MenuAdapter(Context context, int textViewResourceId, ArrayList<Menus> items){
			super(context, textViewResourceId, items);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View v = convertView;
			if (v == null){
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			Menus m = menusList.get(position);
			if (m != null){
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView mm = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null){
					tt.setText(m.getDate());
					tt.setTextColor(Color.parseColor(m.gettxtColor()));
				}
				if (mm != null){
					mm.setText(m.getMenu());
					mm.setTextColor(Color.parseColor("#000000"));
				}
			}
			return v;
		}
	}

	class DownThread extends Thread{
		
		StringBuilder menubuilder = new StringBuilder();
		
		int[] startDate = {cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK)};
		
		public void run(){
			
			//���۳�¥����//
			if (cal.get(Calendar.DAY_OF_WEEK) < 2){
				startDate = moveTheDate(startDate, -6);
			}else if (cal.get(Calendar.DAY_OF_WEEK) > 2){
				startDate = moveTheDate(startDate, 2 - cal.get(Calendar.DAY_OF_WEEK));
			}			
			
			//�޴�Fetch//
			try{
				FileInputStream in = openFileInput("menus.txt");
				
				//Fileinputstream�� String���� ��ȯ. �����ּ� -> http://nmshome.tistory.com/entry/FileInputStream-%EC%9D%B4%EC%9A%A9%EC%82%AC%EB%A1%80
				StringTokenizer st;				
				int count = in.available();
				byte b[] = new byte[count];
				int total = in.read(b);
				String prt = new String(b);
				byte tkb[] = {0x0d, 0x0a};
				String tk = new String(tkb);
				st = new StringTokenizer(prt,tk);
				int sk = st.countTokens();
				for(int i = 0 ; i < sk ; i++){
					menubuilder.append(st.nextToken());
					menubuilder.append("\n");
				}
					
				in.close();
				
			} catch (Exception e) {
				menubuilder.append("FileError");
			}
			
			//�޴�Fetch �Ϸ�//
			
			mAfterDown.sendEmptyMessage(0);
		}
		
		
		Handler mAfterDown = new Handler(){
			public void handleMessage(Message msg){
				mProgress.dismiss();
				
				menusList = new ArrayList<Menus>();
				Menus m = new Menus("���� �̰� ���̸�", "�ݵ�� �����ڿ��� �����϶�� �ϼ���.", "#ffffff");
				
				
				for (int i = 0 ; i < 7 ; i++){
					if (menubuilder.toString().contains("StrError")){
						m = new Menus(dateToString(moveTheDate(startDate, i)), "�޴���������\nȨ������ ������ �ٲ������\n�����ڿ��� �˷��ּ���^^\n������ �̸����� heostar@gmail.com�Դϴ�~", "#ff0000");
						menusList.add(m);
					}else if (menubuilder.toString().contains("NetError")){
						m = new Menus(dateToString(moveTheDate(startDate, i)), "��Ʈ��ũ��ַ� ���� �޴���������\n��Ʈ��ũ ���� ���¸� Ȯ�����ּ���!!", "#aaaaaa");
						menusList.add(m);
					}else if (menubuilder.toString().contains("FileError")){
						m = new Menus(dateToString(moveTheDate(startDate, i)), "�Ĵ��� �ٿ���� �ʾҽ��ϴ�.\n���� �ٽ� ������� �ٿ�޾� �ּ���.", "#aaaaaa");
						menusList.add(m);
					}else {
						int[] Ddate = moveTheDate(startDate,i);
						
						int dStart = menubuilder.indexOf(Ddate[1] + "." + Ddate[2]);
						int mStart = menubuilder.indexOf("m", dStart);
						int mEnd = menubuilder.indexOf("e", mStart);						
						String mEnu = menubuilder.substring(mStart + 1, mEnd);
						
						if (moveTheDate(startDate, i)[3] == 1){
							m = new Menus(dateToString(moveTheDate(startDate, i)), mEnu, "#ff0000");
							menusList.add(m);						
						}else if (moveTheDate(startDate, i)[3] == 7){
							m = new Menus(dateToString(moveTheDate(startDate, i)), mEnu, "#7777ff");
							menusList.add(m);						
						}else {
							m = new Menus(dateToString(moveTheDate(startDate, i)), mEnu, "#000000");
							menusList.add(m);
						}						
					}
				}
				
				
				m_adapter = new MenuAdapter(MearyFoodWeekly.this, R.layout.row, menusList);		
				ListView list = (ListView)findViewById(R.id.weeklylist);
				list.setAdapter(m_adapter);
				
			}
		};
		
	}
	
	
	
	public int[] moveTheDate (int[] thedate, int movement){
		
		int[] daty = new int[4];
		daty[0] = thedate[0];
		daty[1] = thedate[1];
		daty[2] = thedate[2];
		daty[3] = thedate[3];
		
		int[] orgdaty = new int[4];
		orgdaty[0] = cal.get(Calendar.YEAR);
		orgdaty[1] = cal.get(Calendar.MONTH);
		orgdaty[2] = cal.get(Calendar.DATE);
		orgdaty[3] = cal.get(Calendar.DAY_OF_WEEK);
		
		if (movement > 0){
			
			if (daty[1] == 11){
				if (daty[2] + movement > 31){
					daty[0]++;
					daty[1] = 0;
					daty[2] = daty[2] + movement - 31;					
				}else{
					daty[2] = daty[2] + movement;
				}
			}else {
				cal.set(Calendar.MONTH, daty[1]);
				if (daty[2] + movement > cal.getActualMaximum(Calendar.DATE)){
					daty[1]++;
					daty[2] = daty[2] + movement - cal.getActualMaximum(Calendar.DATE);					
				}else{
					daty[2] = daty[2] + movement;
				}
				cal.set(Calendar.MONTH, orgdaty[1]);				
			}
			
		}else if (movement < 0){
			
			if (daty[1] == 0){
				if (daty[2] + movement <= 0){
					daty[0]--;
					daty[1] = 11;
					daty[2] = 31 + daty[2] + movement;					
				}else{
					daty[2] = daty[2] + movement;
				}
			}else {
				if (daty[2] + movement <= 0){
					cal.set(orgdaty[0], orgdaty[1] - 1, 1);
					daty[1]--;
					daty[2] = cal.getActualMaximum(Calendar.DATE) + daty[2] + movement;
					cal.set(orgdaty[0], orgdaty[1], orgdaty[2]);
				}else{
					daty[2] = daty[2] + movement;
				}
			}
		}
		
		cal.set(daty[0], daty[1], daty[2]);
		daty[3] = cal.get(Calendar.DAY_OF_WEEK);
		cal.set(orgdaty[0], orgdaty[1], orgdaty[2]);
		
		return daty;
	}

	public String dateToString (int[] date){
		
		StringBuilder dayOfWeek = new StringBuilder();
		if (date[3] == 1){
			dayOfWeek.append("�Ͽ���");
		}else if(date[3] == 2){
			dayOfWeek.append("������");
		}else if(date[3] == 3){
			dayOfWeek.append("ȭ����");
		}else if(date[3] == 4){
			dayOfWeek.append("������");
		}else if(date[3] == 5){
			dayOfWeek.append("�����");
		}else if(date[3] == 6){
			dayOfWeek.append("�ݿ���");
		}else if(date[3] == 7){
			dayOfWeek.append("�����");
		}
		int month = date[1] + 1;
		return(date[0] + "�� " + month + "�� " + date[2] + "�� " + dayOfWeek.toString());
	}
}


	class Menus{
		private String Date;
		private String Menu;
		private String txtColor;
		
		public Menus(String Date, String Menu, String txtColor){
			this.Date = Date;
			this.Menu = Menu;
			this.txtColor = txtColor;
		}
		
		public String getDate(){
			return Date;
		}
		
		public String getMenu(){
			return Menu;
		}
		
		public String gettxtColor(){
			return txtColor;
		}
	}