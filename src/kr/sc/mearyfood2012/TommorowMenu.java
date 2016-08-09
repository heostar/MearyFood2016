package kr.sc.mearyfood2012;
import java.io.*;
import java.net.*;
import java.util.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;

public class TommorowMenu extends Activity {
	ProgressDialog mProgress;
	DownThread mThread;	
	
		
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);      
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    
	    //���ϳ�¥����
	    Calendar cal = new GregorianCalendar();
	    int yEar = cal.get(Calendar.YEAR);
	    int mOnth = cal.get(Calendar.MONTH);
	    int dAte = cal.get(Calendar.DATE);
	    
	    if (mOnth == 11){
	    	if (dAte == 31){
	    		yEar++;
	    		mOnth = 0;
	    		dAte = 1;
	    	}else {
	    		dAte++;
	    	}
	    }else if (dAte == cal.getActualMaximum(Calendar.DATE)){
	    	mOnth++;
	    	dAte = 1;
	    }else {
	    	dAte++;
	    }
	    //���ϳ�¥�����Ϸ�
	    
	    mProgress = ProgressDialog.show(TommorowMenu.this, "��ٷ��ּ���~!", "�ε����Դϴ�...\n(�ִ�ҿ�ð� 10��)");
	    mThread = new DownThread(mOnth, dAte);
	    mThread.start();
	    	    	    
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
						menus = new StringBuilder("�޴���������\n�޾Ƹ��б� ���� �ٽ� �����ϰų�\n������ Ŭ���˾��޴��� ����\n�Ĵ��� ������Ʈ���ּ���!");
					}
				}catch (Exception e){
					menus.append("�޴���������\n�޾Ƹ��б� ���� �ٽ� �����ϰų�\n������ Ŭ���˾��޴��� ����\n�Ĵ��� ������Ʈ���ּ���!");
				}
				
			}else {
				menus.append("�޴���������\n�޾Ƹ��б� ���� �ٽ� �����ϰų�\n������ Ŭ���˾��޴��� ����\n�Ĵ��� ������Ʈ���ּ���!");
			}
			mAfterDown.sendEmptyMessage(0);
		}
		
		Handler mAfterDown = new Handler(){
			public void handleMessage(Message msg){
				mProgress.dismiss();
				 new AlertDialog.Builder(TommorowMenu.this)
				    .setTitle("������ �Ĵ�")
				    .setMessage(menus.toString())
				    .setOnCancelListener(new DialogInterface.OnCancelListener() {			
						public void onCancel(DialogInterface dialog) {
							finish();				
						}
					})
				    .show();
			}
		};
	
	}
}