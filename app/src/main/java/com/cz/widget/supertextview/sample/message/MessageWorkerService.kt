package com.cz.widget.supertextview.sample.message

import java.util.*

/**
 * 日志写入服务对象
 */
internal class MessageWorkerService : Thread("MessageManager worker") {

    companion object {
        /**
         * 最大只存100条,100条之后阻塞
         */
        private const val MAX_CAPACITY=1
        /**
         * 最长记录等级时间,保证记录时间偏差不长
         */
        private const val MAX_WAIT_TIME=2*60*1000L
        /**
         * 同步锁对象
         */
        private val LOCK = Object()
    }
    /**
     * 消息队列
     */
    private val messageQueue = LinkedList<String>()
    /**
     * 操作时间,用于比较最大时间
     */
    private var activeTimeMillis:Long=0L
    /**
     * 消息输出监听
     */
    private var listenerList= mutableListOf<OnMessageListener>()
    /**
     * 当前服务是否运行
     */
    @Volatile
    private var isRunning=false

    override fun run() {
        super.run()
        //检测project对象
        try {
            //遍历消息
            while(!interrupted()){
                //检测并写入消息
                flushMessageAndWait()
                //如果检测到停止任务，弹出
                if(!isRunning){
                    break
                }
            }
        } catch (e:Exception){
            //此处可以采集异常
            e.printStackTrace()
        } finally {
            //写入剩下所有文件
            flushMessage()
        }
    }

    /**
     * 检测并写入消息
     */
    private fun flushMessageAndWait() {
        if (messageQueue.isNotEmpty()) {
            synchronized(LOCK) {
                while (!messageQueue.isEmpty()) {
                    val message = messageQueue.pollFirst()
                    listenerList.forEach { listener->
                        listener.onMessage(message)
                    }
                }
                //记录操作时间
                activeTimeMillis = System.currentTimeMillis()
                //等待
                LOCK.wait(MAX_WAIT_TIME)
            }
        }
    }

    /**
     * 检测并写入消息
     */
    private fun flushMessage() {
        if (messageQueue.isNotEmpty()) {
            //取出所有消息并记录
            while (!messageQueue.isEmpty()) {
                val message = messageQueue.pollFirst()
                listenerList.forEach { listener->
                    listener.onMessage(message)
                }
            }
        }
    }


    /**
     * 启动服务
     */
    fun startService(){
        //启动服务
        if(!isAlive){
            //记录启动时间
            activeTimeMillis=System.currentTimeMillis()
            //设置运行标记
            isRunning=true
            this.start()
        }
    }

    /**
     * 停止服务
     */
    fun stopService(){
        synchronized(LOCK){
            isRunning=false
            LOCK.notify()
        }
    }

    /**
     * 添加消息到队列
     */
    fun postMessage(message:String){
        synchronized(LOCK){
            //添加消息到队列
            messageQueue.offerLast(message)
            if(System.currentTimeMillis()-activeTimeMillis> MAX_WAIT_TIME){
                //超过最大时间通知
                LOCK.notify()
            } else if(messageQueue.size>= MAX_CAPACITY){
                //超过最大个数通知
                LOCK.notify()
            }
        }
    }

    /**
     * 主动通知唤醒
     */
    fun notifyService(){
        synchronized(LOCK){
            LOCK.notify()
        }
    }

    /**
     * 设置消息输出监听
     */
    fun addOnMessageListener(listener: OnMessageListener){
        synchronized(LOCK){
            this.listenerList.add(listener)
        }
    }

    /**
     * 移除消息输出监听
     */
    fun removeOnMessageListener(listener: OnMessageListener){
        synchronized(LOCK){
            this.listenerList.remove(listener)
        }
    }

    /**
     * 输出消息监听
     */
    interface OnMessageListener{
        fun onMessage(message:String)
    }
}