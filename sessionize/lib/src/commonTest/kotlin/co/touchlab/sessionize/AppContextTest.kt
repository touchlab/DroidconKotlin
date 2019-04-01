package co.touchlab.sessionize

import co.touchlab.sessionize.AppContext.initPlatformClient
import co.touchlab.sessionize.AppContext.loadSchedule
import co.touchlab.sessionize.AppContext.loadSpeakers
import co.touchlab.sessionize.AppContext.loadSponsors
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.db.SqlPreparedStatement
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

var staticFileLoader: (filePrefix: String, fileType: String) -> String? = { _, _ ->   """
    [{
    }]
""".trimIndent()}
/*
    "groupName":"test",
    "sponsors":[],
    "icon":"test",
    "title":"test",
    "detail":"test",
    "date":"test",
    "rooms":[{ "id":"", "name":"", "sessions": []}]
    "id":"",
    "firstName":"",
    "lastName":"",
    "bio":"",
    "tagLine":"",
    "links":[{ "title":"", "url":"", "linkType":"" }],
    "profilePicture":"",
    "fullName":""

 */

@RunWith(AndroidJUnit4::class)
class AppContextTests {


    @BeforeTest
    fun setUp() {
        assertNotNull(staticFileLoader, "staticFileLoader not initialized")
        localStaticFileLoader?.let {
            staticFileLoader = it
        }
        prepareApp()
        initPlatformClient(staticFileLoader, { _, _ -> Unit }, { Unit },
                object: CoroutineDispatcher() {
                    override fun dispatch(context: CoroutineContext, block: Runnable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                },
                object: SqlDriver {
                    override fun close() {
                    }

                    override fun currentTransaction(): Transacter.Transaction? {
                        return null
                    }

                    override fun execute(identifier: Int?, sql: String, parameters: Int, binders: (SqlPreparedStatement.() -> Unit)?) {
                    }

                    override fun executeQuery(identifier: Int?, sql: String, parameters: Int, binders: (SqlPreparedStatement.() -> Unit)?): SqlCursor {
                        return object : SqlCursor {
                            override fun close() {
                            }

                            override fun getBytes(index: Int): ByteArray? {
                                return byteArrayOf()
                            }

                            override fun getDouble(index: Int): Double? {
                                return 0.0
                            }

                            override fun getLong(index: Int): Long? {
                                return 0
                            }

                            override fun getString(index: Int): String? {
                                return ""
                            }

                            override fun next(): Boolean {
                                return false
                            }
                        }

                    }

                    override fun newTransaction(): Transacter.Transaction {
                        return object: Transacter.Transaction() {
                            override val enclosingTransaction: Transacter.Transaction?
                                get() = null

                            override fun endTransaction(successful: Boolean) {
                                println("endTransaction --------------------------")
                            }

                        }
                    }
                })
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun testSponsors() {
        val sponsors = loadSponsors()
        sponsors?.let {
            val sponsorsJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(sponsorsJson.size, 0, "empty sponsors.json or none found")
            assertTrue(sponsorsJson[0].jsonObject.containsKey("groupName"))
            assertTrue(sponsorsJson[0].jsonObject.containsKey("sponsors"))
        }
    }

    @Test
    fun testAbout() {
        val about = staticFileLoader.invoke("about", "json")
        about?.let {
            val aboutJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(aboutJson.size, 0, "empty about.json or none found")
            assertTrue(aboutJson[0].jsonObject.containsKey("icon"))
            assertTrue(aboutJson[0].jsonObject.containsKey("title"))
            assertTrue(aboutJson[0].jsonObject.containsKey("detail"))
        }
    }

    @Test
    fun testSchedule() {
        val schedule = loadSchedule()
        schedule?.let {
            val scheduleJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(scheduleJson.size, 0, "empty schedule.json or none found")
            assertTrue(scheduleJson[0].jsonObject.containsKey("date"))
            assertTrue(scheduleJson[0].jsonObject.containsKey("rooms"))
        }
    }


    @Test
    fun testSpeakers() {
        val speakers = loadSpeakers()
        speakers?.let {
            val speakersJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(speakersJson.size, 0, "empty speakers.json or none found")
            assertTrue(speakersJson[0].jsonObject.containsKey("id"))
            assertTrue(speakersJson[0].jsonObject.containsKey("firstName"))
            assertTrue(speakersJson[0].jsonObject.containsKey("lastName"))
        }
    }

}

