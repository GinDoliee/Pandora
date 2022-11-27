package ts.thunder.storm.pandora

class Stake(address:String) {

    lateinit var FCT_Address:String

    var coinAvailableAmount:Float = 0.0F
    var coinDelegatedAmount:Float = 0.0F
    var coinRewardAmount:Float = 0.0F

    init {
        FCT_Address = address
    }
}