package com.cz.widget.supertextview.sample.template

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cz.widget.supertextview.sample.R

/**
 * Created by cz on 2017/6/26.
 */
open class ToolBarActivity : AppCompatActivity(){
    lateinit var toolbar: Toolbar
    private lateinit var indeterminate: ProgressBar
    private var toolBarLayout = R.layout.sample_toolbar
    private var overFlow: Boolean = false

    override fun setContentView(@LayoutRes layoutResID: Int) {
        val layout = RelativeLayout(this)
        //初始化content
        val contentView = layoutInflater.inflate(layoutResID, layout, false)
        val layoutParams2 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        if (!overFlow) {
            layoutParams2.addRule(RelativeLayout.BELOW, R.id.toolBar)
        }
        layout.addView(contentView, layoutParams2)

        //初始化toolbar
        toolbar = layoutInflater.inflate(toolBarLayout, layout, false) as Toolbar

        //初始化indeterminate
        indeterminate = ProgressBar(this, null, R.attr.progressBarStyle)
        indeterminate.setPadding(dip2px(12f).toInt(), dip2px(12f).toInt(), dip2px(12f).toInt(), dip2px(12f).toInt())
        indeterminate.visibility = View.GONE
        val layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        toolbar.addView(indeterminate, layoutParams)
        layout.addView(toolbar)
        setSupportActionBar(toolbar)
        val title=intent.getStringExtra("title")
        val desc=intent.getStringExtra("desc")
        supportActionBar?.title = title
        supportActionBar?.subtitle = desc
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        setContentView(layout)
    }

    fun dip2px(value:Float)=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,resources.displayMetrics)


    /**
     * 设置toolbar悬浮,必须在setContentView方法之前回调生效
     * @param overFlow
     */
    fun setToolBarOverFlow(overFlow: Boolean) {
        this.overFlow = overFlow
    }

    /**
     * 设置自定义的toolbar布局对象,必须在setContentView方法之前回调生效
     * @param layout
     */
    fun setCustomToolBar(@LayoutRes layout: Int) {
        this.toolBarLayout = layout
    }

    override fun setTitle(@StringRes res: Int) {
        toolbar.setTitle(res)
    }

    fun setTitle(title: String) {
        toolbar.title = title
    }

    fun setLogo(@DrawableRes res: Int) {
        toolbar.setLogo(res)
    }

    fun setLogo(drawable: Drawable) {
        toolbar.logo = drawable
    }

    fun setNavigationIcon(@DrawableRes res: Int) {
        toolbar.setLogo(res)
    }

    fun setNavigationIcon(drawable: Drawable) {
        toolbar.navigationIcon = drawable
    }

    fun setNavigationOnClickListener(listener: View.OnClickListener) {
        toolbar.setNavigationOnClickListener(listener)
    }

    /**
     * 是否显示加载旋转框

     * @param showIndeterminate
     */
    fun showIndeterminate(showIndeterminate: Boolean) {
        indeterminate.visibility = if (showIndeterminate) View.VISIBLE else View.GONE
    }

    fun getToolBar(): Toolbar {
        return toolbar
    }
}