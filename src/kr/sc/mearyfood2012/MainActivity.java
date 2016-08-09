package kr.sc.mearyfood2012;

import java.io.*;
import java.net.*;
import java.util.*;


import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
	ProgressDialog mProgress;
	DownThread mThread;	
	ArrayList<String> mainMenu;	
	ArrayAdapter<String> Adapter;
	ListView list;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		mainMenu = new ArrayList<String>();
		mainMenu.add("오늘 메뉴 보기");
		mainMenu.add("내일 메뉴 보기");
		mainMenu.add("이번주 메뉴 보기");
		mainMenu.add("이번달 메뉴 보기");
		mainMenu.add("앱 안내 및 후기");
				
		
		Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mainMenu);
		
		list = (ListView)findViewById(R.id.mainlist);
		list.setAdapter(Adapter);	
		list.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView parent, View view, int position, long id){
		
		if (position == 0){
			Calendar cal = new GregorianCalendar();
		    int mOnth = cal.get(Calendar.MONTH);
		    int dAte = cal.get(Calendar.DATE);
			 mProgress = ProgressDialog.show(MainActivity.this, "기다려주세요~!", "다운로드중입니다...\n(최대소요시간 10초)");
			 mThread = new DownThread(mOnth, dAte);
			 mThread.start();
		}else if(position == 1){
			Intent intenty = new Intent();
			intenty.setClass(getApplicationContext(), TommorowMenu.class);
			startActivity(intenty);
		}else if(position == 2){
			Intent intent = new Intent(this, MearyFoodWeekly.class);
			startActivity(intent);
		}else if(position == 3){
			Intent intent = new Intent(this, MearyFoodMonthly.class);
			startActivity(intent);
		}else if(position == 4){
			Intent intent = new Intent(this, Information.class);
			startActivity(intent);
		}
		
	}
	
	class DownThread extends Thread {
		
		int month;
		int date;
		StringBuilder menus = new StringBuilder();
		
		DownThread(int month, int date){
			this.month = month;
			this.date = date;
		}
		
		public void run() {
			boolean file_exists = false;
			boolean no_error = true;
			FileInputStream in;
			
			try{
				in = openFileInput("menus.txt");
				in.close();
				file_exists = true;
			} catch (Exception e) {
				file_exists = false;
			}
			
			if (file_exists) {
				try{
					in = openFileInput("menus.txt");
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
						menus.append(st.nextToken());
						menus.append("\n");
					}					
					in.close();	
					
					no_error = !menus.toString().contains("Error");
					no_error = no_error && menus.toString().contains("m");
					
					if (no_error){
						int start = menus.indexOf(month + "." + date);
						start = menus.indexOf("m", start + 1);
						int end = menus.indexOf("e", start);
						menus = new StringBuilder(menus.substring(start + 1, end));
					}else {
						menus = new StringBuilder("메뉴생성실패\n앱을 다시 실행하거나\n위젯의 클릭팝업메뉴를 통해\n식단을 업데이트해주세요!");
					}
				}catch (Exception e){
					menus.append("메뉴생성실패\n앱을 다시 실행하거나\n위젯의 클릭팝업메뉴를 통해\n식단을 업데이트해주세요!");
				}
				
			}else {
				menus.append("메뉴생성실패\n앱을 다시 실행하거나\n위젯의 클릭팝업메뉴를 통해\n식단을 업데이트해주세요!");
			}
			
			mAfterDown.sendEmptyMessage(0);
		}
		
		Handler mAfterDown = new Handler(){
			public void handleMessage(Message msg){
				mProgress.dismiss();
				 new AlertDialog.Builder(MainActivity.this)
				    .setTitle("오늘의 식단")
				    .setMessage(menus.toString())
				    .show();
			}
		};
	
	}
}


