package kr.sc.mearyfood2012;

import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FoodWidgetConfigure extends Activity {
	
	ArrayAdapter<CharSequence> adspin;
	final static String PREF = "kr.sc.mearyfood2012";
	int mId;
	ImageView IV;
	TextView txtdate;
	TextView txt1;
	TextView txt2;
	TextView txt3;
	TextView txt4;
	TextView txt5;
	TextView txtDName;
	TextView txt7;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setResult(RESULT_CANCELED);
		
		Intent intent = getIntent();
		mId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		
		CharSequence[] modes = {"자동모드 - 데이터 많이 사용(하루최대0.3MB)", "데이터절약모드 - 앱 실행시 업데이트"};
		new AlertDialog.Builder(this)
		.setTitle("위젯 업데이트 유형을 선택해주세요")
		.setSingleChoiceItems(modes, -1, ClickListener)
		.show();
		
		
		IV = (ImageView)findViewById(R.id.previewimage);
		txtdate = (TextView)findViewById(R.id.configdate);
		txt1 = (TextView)findViewById(R.id.configmenu1);
		txt2 = (TextView)findViewById(R.id.configmenu2);
		txt3 = (TextView)findViewById(R.id.configmenu3);
		txt4 = (TextView)findViewById(R.id.configmenu4);
		txt5 = (TextView)findViewById(R.id.configmenu5);
		txtDName = (TextView)findViewById(R.id.configmenuDName);
		txt7 = (TextView)findViewById(R.id.configmenu7);
		final TextView[] txtarray = {txtdate, txt1, txt2, txt3, txt4, txt5, txtDName, txt7};
		
		Spinner spin = (Spinner)findViewById(R.id.stylespinner);
		spin.setPrompt("스타일을 고르세요.");
		
		adspin = ArrayAdapter.createFromResource(this, R.array.Style, android.R.layout.simple_spinner_item);
		adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adspin);
		
		spin.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				SharedPreferences prefs = getSharedPreferences(PREF, 0);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putLong("bG_" + mId , adspin.getItemId(position));
				editor.commit();
								
				changePreview(IV, txtarray, adspin.getItemId(position));
			}
			
			public void onNothingSelected(AdapterView<?> parent){
				
			}
		});
				
	}
	
	class ClickListenerClass implements OnClickListener{
				
		public void onClick(DialogInterface dialog, int which) {
			
			if (which == 0){
				try{
					FileOutputStream fos = openFileOutput("widgetMode.txt", Context.MODE_WORLD_READABLE);
					fos.write("자동모드".getBytes());
					fos.close();
				}catch (Exception e) {
					Toast.makeText(FoodWidgetConfigure.this, "위젯모드생성실패. 다시 위젯을 생성해주세요!", Toast.LENGTH_LONG).show();
				}
				Toast.makeText(FoodWidgetConfigure.this, "자동업데이트를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}else if (which == 1){
				try{
					FileOutputStream fos = openFileOutput("widgetMode.txt", Context.MODE_WORLD_READABLE);
					fos.write("절약모드".getBytes());
					fos.close();
				}catch (Exception e) {
					Toast.makeText(FoodWidgetConfigure.this, "위젯모드생성실패. 다시 위젯을 생성해주세요!", Toast.LENGTH_LONG).show();
				}
				Toast.makeText(FoodWidgetConfigure.this, "데이터절약모드를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}						
		}
	}
	
	ClickListenerClass ClickListener = new ClickListenerClass();
	
	
	
	public void instClick(View v) {
		switch (v.getId()){
		case R.id.confirmbtn:
			Context con = FoodWidgetConfigure.this;
			FoodWidget x = new FoodWidget();
			x.UpdateMenus(con, AppWidgetManager.getInstance(con), mId);
			
			Intent intent = new Intent();
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mId);
			setResult(RESULT_OK, intent);
			finish();
			
			break;
		}
	}
	
	public static void changePreview (ImageView IV, TextView[] txtarray, Long target){
		
		if (target == 0){
			IV.setImageResource(R.drawable.frame2x2ylw_100);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("메아리Yellow");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 1){
			IV.setImageResource(R.drawable.frame2x2ylw_75);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("메아리Yellow");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 2){
			IV.setImageResource(R.drawable.frame2x2ylw_50);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("메아리Yellow");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 3){
			IV.setImageResource(R.drawable.frame2x2blk_100);
			String strColor = "#e6e6e6";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("와인Black");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 4){
			IV.setImageResource(R.drawable.frame2x2blk_75);
			String strColor = "#e6e6e6";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("와인Black");
			txtarray[6].setText("허공익☆");
		}		
		else if(target == 5){
			IV.setImageResource(R.drawable.frame2x2blk_50);
			String strColor = "#e6e6e6";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("와인Black");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 6){
			IV.setImageResource(R.drawable.frame2x2pnk_100);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("딸기Pink");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 7){
			IV.setImageResource(R.drawable.frame2x2pnk_75);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("딸기Pink");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 8){
			IV.setImageResource(R.drawable.frame2x2pnk_50);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("딸기Pink");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 9){
			IV.setImageResource(R.drawable.frame2x2wht_100);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("크리스마스White");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 10){
			IV.setImageResource(R.drawable.frame2x2wht_75);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("크리스마스White");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 11){
			IV.setImageResource(R.drawable.frame2x2wht_50);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("크리스마스White");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 12){
			IV.setImageResource(R.drawable.frame2x2prpl_100);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("블루베리Purple");
			txtarray[6].setText("선희쌤^^");
		}
		else if(target == 13){
			IV.setImageResource(R.drawable.frame2x2prpl_75);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("블루베리Purple");
			txtarray[6].setText("선희쌤^^");
		}
		else if(target == 14){
			IV.setImageResource(R.drawable.frame2x2prpl_50);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("블루베리Purple");
			txtarray[6].setText("선희쌤^^");
		}
		else if(target == 15){
			IV.setImageResource(R.drawable.frame2x2tpw);
			String strColor = "#ffffff";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("투명White");
			txtarray[6].setText("허공익☆");
		}
		else if(target == 16){
			IV.setImageResource(R.drawable.frame2x2tpb);
			String strColor = "#000000";
			for (int i = 0 ; i < txtarray.length ; i++){
				txtarray[i].setTextColor(Color.parseColor(strColor));
			}
			txtarray[4].setText("투명Black");
			txtarray[6].setText("허공익☆");
		}
	}
}