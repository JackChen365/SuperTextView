package com.cz.widget.supertextview.sample.message

/**
 * @author Created by cz
 * @date 2019-09-16 14:35
 * @email bingo110@126.com
 * 日志信息管理类
 */
internal object MessageManager{
    /**
     * 日志工作线程
     */
    private var workerThread=MessageWorkerService()

    /**
     * 启动日志服务
     */
    fun start(){
        //启动日志服务
        if(!workerThread.isAlive){
            workerThread.startService()
        }
    }

    /**
     * 停止服务
     */
    fun stop(){
        if(workerThread.isAlive){
            workerThread.stopService()
        }
    }

    fun isRunning():Boolean{
        return workerThread.isAlive
    }

    /**
     * 发送一个消息
     */
    fun post(message:String){
        workerThread.postMessage(message)
    }

    /**
     * 设置消息输出监听
     */
    fun addOnMessageListener(listener: MessageWorkerService.OnMessageListener){
        this.workerThread.addOnMessageListener(listener)
    }

    /**
     * 设置消息输出监听
     */
    fun removeOnMessageListener(listener: MessageWorkerService.OnMessageListener){
        this.workerThread.removeOnMessageListener(listener)
    }
}