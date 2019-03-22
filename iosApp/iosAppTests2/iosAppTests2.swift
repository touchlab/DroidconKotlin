//
//  iosAppTests2.swift
//  iosAppTests2
//
//  Created by Philip K. Han on 3/22/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import XCTest
import test

class iosAppTests2: XCTestCase {

    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() {
        AppContextTestKt.staticFileLoader = loadAsset
        AppContextTestKt.kickOffTest()
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }
    
    func loadAsset(filePrefix:String, fileType:String) -> String?{
        do{
            let bundleFile = Bundle.main.path(forResource: filePrefix, ofType: fileType)
            return try String(contentsOfFile: bundleFile!)
        } catch {
            return nil
        }
    }

    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

}
