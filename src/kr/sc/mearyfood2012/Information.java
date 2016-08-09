package kr.sc.mearyfood2012;
import java.util.*;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class Information extends Activity implements AdapterView.OnItemClickListener{
	
	ArrayList<String> infoList;	
	ArrayAdapter<String> infoAdapter;
	ListView infolist;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information);
		
		infoList = new ArrayList<String>();
		infoList.add("식단업데이트 안내");
		infoList.add("어플 사용법");		
		infoList.add("개발자 후기");
		
		infoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, infoList);
		
		infolist = (ListView)findViewById(R.id.listView_info);
		infolist.setAdapter(infoAdapter);	
		infolist.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView parent, View view, int position, long id){
		
		if(position == 0){
			new AlertDialog.Builder(this)
			.setTitle("식단업데이트 안내")
			.setMessage(R.string.guide2)
			.setNegativeButton("닫기", null)
			.show();
		}else if(position == 1){
			new AlertDialog.Builder(this)
			.setTitle("어플 사용법")
			.setMessage(R.string.guide1)
			.setNegativeButton("닫기", null)
			.show();
		}else if(position == 2){
			new AlertDialog.Builder(this)
			.setTitle("개발자 후기")
			.setMessage(R.string.hugi)
			.setNegativeButton("닫기", null)
			.show();
		}
		
	}
}
