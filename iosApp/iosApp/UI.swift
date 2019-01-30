//
//  UI.swift
//  iosApp
//
//  Created by Kevin Galligan on 11/3/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import Foundation
import main

public class UI: Kotlinx_coroutines_core_nativeCoroutineDispatcher {
    override public func dispatch(context: KotlinCoroutineContext, block: Kotlinx_coroutines_core_nativeRunnable) {
        DispatchQueue.main.async {
            block.run()
        }
    }
}
