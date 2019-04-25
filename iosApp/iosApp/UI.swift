//
//  UI.swift
//  iosApp
//
//  Created by Kevin Galligan on 11/3/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import Foundation
import lib

public class UI: Kotlinx_coroutines_coreCoroutineDispatcher {
    override public func dispatch(context: KotlinCoroutineContext, block: Kotlinx_coroutines_coreRunnable) {
        DispatchQueue.main.async {
            block.run()
        }
    }
}
