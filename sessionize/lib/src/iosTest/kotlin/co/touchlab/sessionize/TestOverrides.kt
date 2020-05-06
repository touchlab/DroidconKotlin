package co.touchlab.sessionize

import kotlin.test.BeforeTest

class StaticFileLoaderTestDarwin : StaticFileLoaderTest() {
    @BeforeTest
    fun androidSetup(){
        setUp()
    }
}

//class EventModelTestJVM: EventModelTest()