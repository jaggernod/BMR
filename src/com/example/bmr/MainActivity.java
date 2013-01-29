package com.example.bmr;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText mHeightEditor;
	EditText mWeightEditor;
	EditText mAgeEditor;
	RadioGroup mRadioGroup;
	TextView mBmrText;
	TextView mCalorieReqText;
	SeekBar mActivityLvl;
	
	int mCalculatedBmr = 0;
	int mCalculatedCalorieNeed = 0;

	TextWatcher updateWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void afterTextChanged(Editable s) {
			updateCalculation();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHeightEditor = (EditText) findViewById(R.id.editHeigth);
		mWeightEditor = (EditText) findViewById(R.id.editWeigth);
		mAgeEditor = (EditText) findViewById(R.id.editAge);
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		mBmrText = (TextView) findViewById(R.id.textResult);
		mCalorieReqText = (TextView) findViewById(R.id.textCalorieReq);
		mActivityLvl = (SeekBar) findViewById(R.id.seekBarActivityLvl);

		mHeightEditor.addTextChangedListener(updateWatcher);
		mWeightEditor.addTextChangedListener(updateWatcher);
		mAgeEditor.addTextChangedListener(updateWatcher);
		
		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				updateCalculation();
			}
		});
		mActivityLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				updateCalculation();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		getMenuInflater().inflate(R.menu.main_manu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		share();
		return super.onOptionsItemSelected(item);
	}

	public int calculateRevisedHarrisBenedict(double height, double weight,
			int age, boolean isWoman) {
		final double weightConstant = isWoman ? 9.247 : 13.397;
		final double heightConstant = isWoman ? 3.098 : 4.799;
		final double ageConstant = isWoman ? 4.330 : 5.677;
		final double hbConstant = isWoman ? 447.593 : 88.362;

		return (int)(heightConstant * height + weightConstant * weight + ageConstant
				* age + hbConstant);
	}

	public void updateCalculation() {
		try {
			double height = Double.parseDouble(mHeightEditor.getText()
					.toString());
			double weight = Double.parseDouble(mWeightEditor.getText()
					.toString());
			int age = Integer.parseInt(mAgeEditor.getText().toString());
			boolean isWoman = mRadioGroup.getCheckedRadioButtonId() == R.id.radioFemale;
			int result = calculateRevisedHarrisBenedict(height, weight, age,
					isWoman);
			double activityFactor = mActivityLvl.getProgress() * 0.125 + 1.2;
			
			int calReq = (int) (activityFactor * result);
			
			mCalculatedBmr = result;
			mCalculatedCalorieNeed = calReq;
			
			mBmrText.setText(result + " kCal");
			mCalorieReqText.setText(calReq + " kCal");
		} catch (NumberFormatException e) {
			mBmrText.setText("");
			mCalorieReqText.setText("");
		}
	}
	
	public void share() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"jaggernod@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "This is your calculated BMR value");
		String content = "Your calculated BMR is " 
				+ mCalculatedBmr 
				+ " kCal and your calorie need for daily activity is " 
				+ mCalculatedCalorieNeed 
				+ " kCal";
		i.putExtra(Intent.EXTRA_TEXT   , content);
		try {
		    startActivity(Intent.createChooser(i, "Sending mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Log.i("APP", "Error");
		    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

}
