package com.cz.widget.supertextview.sample.message

import android.os.Bundle
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author Created by cz
 * @date 2019-09-06 16:31
 * @email bingo110@126.com
 * 用于演示示例,处理自动资源释放问题
 */
class MessageLifeCycleBindFragment : Fragment() {
    companion object {

        private const val BIND_SAMPLE_FRAGMENT_TAG = "cz.sample.bind_fragment_tag"

        @Keep
        @JvmStatic
        fun injectIfNeededIn(activity: FragmentActivity) {
            val manager = activity.supportFragmentManager
            if (manager.findFragmentByTag(BIND_SAMPLE_FRAGMENT_TAG) == null) {
                manager.beginTransaction().add(MessageLifeCycleBindFragment(), BIND_SAMPLE_FRAGMENT_TAG).commit()
                manager.executePendingTransactions()
            }
        }

        @JvmStatic
        fun get(activity: FragmentActivity): MessageLifeCycleBindFragment {
            return activity.supportFragmentManager.findFragmentByTag(BIND_SAMPLE_FRAGMENT_TAG) as MessageLifeCycleBindFragment
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val ownerActivity=activity
        if(ownerActivity is MessageWorkerService.OnMessageListener){
            MessageManager.addOnMessageListener(ownerActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val ownerActivity=activity
        if(ownerActivity is MessageWorkerService.OnMessageListener){
            MessageManager.removeOnMessageListener(ownerActivity)
        }
    }
}
