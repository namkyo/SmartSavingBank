package kr.co.smartbank.app.view.push

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.ListView
import kr.co.smartbank.app.R
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.process.DBHelper
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity

class PushHistList : BaseActivity() {
    private lateinit var activity: Activity
    private lateinit var context: Context
    private lateinit var sp: SharedPreferenceHelper
    private lateinit var commonDTO: CommonDTO
    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.push_hist_list)
        activity = this
        context = applicationContext
        sp = SharedPreferenceHelper(activity)
        commonDTO = CommonDTO(activity, supportFragmentManager, sp)



        var clostBtn: View =findViewById(R.id.push_hist_list_btn_close)
        clostBtn.setOnClickListener({
            activity.finish()
            //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        })


        dbHelper = DBHelper(this, "newdb.db", null, 1)
        database = dbHelper.writableDatabase


        var query = "SELECT * FROM push_hist;"
        var c = database.rawQuery(query, null)

        Logcat.d("rawCount : "+c.count)
        if(c.count > 0){
            Logcat.d("데이터 존재")
            var items = mutableListOf<PushHistListItem>()
            while(c.moveToNext()){
                val title : String = c.getString(c.getColumnIndex("title"))
                val body : String = c.getString(c.getColumnIndex("body"))
                val image : String = c.getString(c.getColumnIndex("image"))
                val date : String = c.getString(c.getColumnIndex("date"))
                val dto = PushHistListItem(title, body, date, image)
                Logcat.d("dto : "+dto)
                items.add(dto)
            }
            val adapter = PushHistListAdapter(items)

            var listView: ListView =findViewById(R.id.push_hist_list_content)
            listView.adapter=adapter

        }else{
            var pushYn: View =findViewById(R.id.push_hist_list_yn)
            pushYn.visibility=View.VISIBLE
        }

        //클릭이벤트
        //listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long -> val item = parent.getItemAtPosition(position) as ListViewItem Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show() }

    }

    private fun pushDbSelect(){
        var query = "SELECT * FROM push_hist;"
        var c = database.rawQuery(query, null)
        while(c.moveToNext()){
            Logcat.d("msg : " + c.getString(c.getColumnIndex("msg")))
            Logcat.d("image : " + c.getString(c.getColumnIndex("image")))
        }
    }
}