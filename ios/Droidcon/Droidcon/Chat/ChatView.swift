//
//  ChatView.swift
//  Droidcon
//
//  Created by Kevin Schildhorn on 4/3/24.
//  Copyright © 2024 Touchlab. All rights reserved.
//

import Foundation
import SwiftUI
import StreamChatSwiftUI

struct ChatView: View {
    @State var streamChat: StreamChat?

    init() {
        streamChat = StreamChat(chatClient: ChatManager.instance.chatClient)
    }
    
    var body: some View {
        NavigationView {
            ChatChannelListView()
        }
    }
}
