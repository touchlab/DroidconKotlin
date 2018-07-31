package co.touchlab.sessionize

interface SnackHost{
    fun showSnack(message:String, length:Int)
    fun showSnack(message:Int, length:Int)
}