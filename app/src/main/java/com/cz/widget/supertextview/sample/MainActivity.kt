package com.cz.widget.supertextview.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cz.widget.supertextview.sample.template.FuncTemplate
import com.cz.widget.supertextview.sample.template.SampleItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val funcTemplate = FuncTemplate.getInstance(this)
        val id=intent.getIntExtra("id",0)
        val title = intent.getStringExtra("title")
        if(null==title) {
            toolBar.setTitle(R.string.app_name)
            setSupportActionBar(toolBar)
        } else {
            toolBar.title = title
            toolBar.subtitle=intent.getStringExtra("desc")
            setSupportActionBar(toolBar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolBar.setNavigationOnClickListener{ finish() }
        }
        recyclerView.layoutManager= LinearLayoutManager(this)
        val items = funcTemplate[id]
        if(null!=items){
            recyclerView.adapter=Adapter(this,funcTemplate,items)
        }
    }

    class Adapter(context:Context,private val template: FuncTemplate,private val items:List<SampleItem<Activity>>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val layoutInflater=LayoutInflater.from(context)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view=layoutInflater.inflate(R.layout.sample_item,parent,false)
            return object: RecyclerView.ViewHolder(view){}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item=items[position]
            holder.itemView.findViewById<TextView>(R.id.title).text=item.title
            holder.itemView.findViewById<TextView>(R.id.desc).text=item.desc
            holder.itemView.setOnClickListener {
                val context=it.context
                if(item.id in template){
                    it.context.startActivity(Intent(context,MainActivity::class.java).apply {
                        putExtra("id",item.id)
                        putExtra("title",item.title)
                        putExtra("desc",item.desc)
                    })//子分组
                } else if(null!=item.clazz){
                    it.context.startActivity(Intent(context,item.clazz).apply { putExtra("title",item.title) })//子条目
                } else{
                    Toast.makeText(context,"未配置子分组,也未配置跳转界面信息!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun getItemCount(): Int=items.size


    }
}
