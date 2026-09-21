package com.mohammadbahrami.border;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private LinearLayout list, stats;
    private EditText search;
    private final int purple = Color.rgb(108,60,235);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(34,20,63));
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        db = new DatabaseHelper(this);
        setContentView(buildScreen());
        refresh();
    }

    private View buildScreen() {
        LinearLayout root = vertical(); root.setBackgroundColor(Color.rgb(250,248,255));
        TextView header = text("بازرگانی محمد بهرامی", 24, Color.WHITE); header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(20), dp(20), dp(20), dp(20)); header.setBackgroundColor(Color.rgb(34,20,63));
        root.addView(header, new LinearLayout.LayoutParams(-1, dp(80)));

        stats = new LinearLayout(this); stats.setOrientation(LinearLayout.HORIZONTAL); stats.setGravity(Gravity.CENTER);
        stats.setPadding(dp(8), dp(12), dp(8), dp(8)); root.addView(stats);

        search = new EditText(this); search.setHint("جست‌وجوی پلاک، راننده یا صاحب کالا"); search.setSingleLine(true);
        search.setBackgroundResource(com.mohammadbahrami.border.R.drawable.card_bg); search.setPadding(dp(16),0,dp(16),0);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, dp(54)); sp.setMargins(dp(14),dp(4),dp(14),dp(10)); root.addView(search, sp);
        search.setOnEditorActionListener((v, actionId, event) -> { refresh(); return true; });

        ScrollView scroll = new ScrollView(this); list = vertical(); list.setPadding(dp(12),0,dp(12),dp(100)); scroll.addView(list);
        root.addView(scroll, new LinearLayout.LayoutParams(-1,0,1));

        FloatingActionButton add = new FloatingActionButton(this); add.setImageResource(android.R.drawable.ic_input_add); add.setColorFilter(Color.WHITE); add.setBackgroundTintList(android.content.res.ColorStateList.valueOf(purple));
        add.setOnClickListener(v -> showAdd());
        android.widget.FrameLayout frame = new android.widget.FrameLayout(this); frame.addView(root);
        android.widget.FrameLayout.LayoutParams fp = new android.widget.FrameLayout.LayoutParams(dp(64),dp(64),Gravity.BOTTOM|Gravity.END); fp.setMargins(0,0,dp(22),dp(22)); frame.addView(add,fp);
        return frame;
    }

    private void refresh() {
        stats.removeAllViews();
        for (int i=0;i<3;i++) {
            TextView s = text(DatabaseHelper.STATUSES[i] + "\n" + db.countStatus(i), 13, i==0?purple:Color.DKGRAY);
            s.setGravity(Gravity.CENTER); s.setBackgroundResource(R.drawable.card_bg);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,dp(72),1); p.setMargins(dp(4),0,dp(4),0); stats.addView(s,p);
        }
        list.removeAllViews();
        List<Truck> trucks = db.all(search == null ? "" : search.getText().toString().trim());
        if (trucks.isEmpty()) { TextView empty=text("هنوز ماشینی ثبت نشده\nاز دکمه + اولین ماشین را اضافه کن",16,Color.GRAY); empty.setGravity(Gravity.CENTER); empty.setPadding(0,dp(70),0,0); list.addView(empty); }
        for (Truck t : trucks) list.addView(truckCard(t));
    }

    private View truckCard(Truck t) {
        LinearLayout card=vertical(); card.setBackgroundResource(R.drawable.card_bg); card.setPadding(dp(16),dp(12),dp(16),dp(12));
        TextView title=text("پلاک: " + t.plate + "     " + DatabaseHelper.STATUSES[t.status],18,purple); title.setTypeface(null,1); card.addView(title);
        card.addView(text("راننده: " + safe(t.driver) + "   |   صاحب کالا: " + safe(t.owner),14,Color.DKGRAY));
        card.addView(text("بار: " + safe(t.cargo) + "   |   مبدأ: " + safe(t.origin),14,Color.DKGRAY));
        card.addView(text("پارکینگ: " + safe(t.parking) + "   |   وزن: " + safe(t.weight),14,Color.DKGRAY));
        LinearLayout actions=new LinearLayout(this); actions.setGravity(Gravity.END);
        Button call=new Button(this); call.setText("تماس"); call.setOnClickListener(v->{ if(t.phone==null||t.phone.isEmpty()) Toast.makeText(this,"شماره ثبت نشده",Toast.LENGTH_SHORT).show(); else startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+t.phone))); });
        Button next=new Button(this); next.setText(t.status<DatabaseHelper.STATUSES.length-1 ? "مرحله بعد" : "تکمیل شده"); next.setEnabled(t.status<DatabaseHelper.STATUSES.length-1); next.setOnClickListener(v->{db.advance(t.id,t.status);refresh();});
        actions.addView(call); actions.addView(next); card.addView(actions);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(6),0,dp(8)); card.setLayoutParams(p); return card;
    }

    private void showAdd() {
        LinearLayout form=vertical(); form.setPadding(dp(20),0,dp(20),0);
        String[] labels={"شماره پلاک *","نام راننده","شماره تماس راننده","نوع بار","صاحب کالا","ترخیص‌کار عراقی","مبدأ بارگیری","شماره پارکینگ","وزن"};
        EditText[] f=new EditText[labels.length];
        for(int i=0;i<labels.length;i++){ f[i]=new EditText(this); f[i].setHint(labels[i]); f[i].setSingleLine(true); if(i==2)f[i].setInputType(InputType.TYPE_CLASS_PHONE); form.addView(f[i],new LinearLayout.LayoutParams(-1,dp(54))); }
        ScrollView sv=new ScrollView(this); sv.addView(form);
        AlertDialog d=new AlertDialog.Builder(this).setTitle("ثبت ماشین جدید").setView(sv).setNegativeButton("انصراف",null).setPositiveButton("ثبت",null).create();
        d.setOnShowListener(x->d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            if(f[0].getText().toString().trim().isEmpty()){f[0].setError("پلاک الزامی است");return;}
            db.add(val(f[0]),val(f[1]),val(f[2]),val(f[3]),val(f[4]),val(f[5]),val(f[6]),val(f[7]),val(f[8])); d.dismiss(); refresh();
        })); d.show();
    }

    private String val(EditText e){return e.getText().toString().trim();}
    private String safe(String s){return s==null||s.isEmpty()?"—":s;}
    private LinearLayout vertical(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    private TextView text(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setPadding(dp(4),dp(5),dp(4),dp(5));return v;}
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
}
