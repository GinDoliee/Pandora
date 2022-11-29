package ts.thunder.storm.pandora

class Coin(name:String) {
    var cointName:String
    var coinPrice:Float = 0.0F
    var coinChange:Float = 0.0F
    var coinVolume:Int = 0

    init {
        cointName = name
    }

}