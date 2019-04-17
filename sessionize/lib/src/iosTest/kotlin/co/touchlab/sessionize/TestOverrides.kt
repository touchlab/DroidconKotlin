package co.touchlab.sessionize

import kotlin.test.BeforeTest

class AppContextTestDarwin: AppContextTests(){
    @BeforeTest
    fun androidSetup(){
        setUp()
        AppContext.initAppContext()
    }
}

class EventModelTestJVM: EventModelTest()