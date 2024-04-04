//
//  ChatManager.swift
//  Droidcon
//
//  Created by Kevin Schildhorn on 4/3/24.
//  Copyright © 2024 Touchlab. All rights reserved.
//

import Foundation
import StreamChat
import DroidconKit

class ChatManager {
    
    static let instance = ChatManager()

    var chatClient: ChatClient = {
        
        guard let infoDictionary: [String: Any] = Bundle.main.infoDictionary,
            let key = infoDictionary["streamApiKey"] as? String,
            !key.isEmpty else {
            fatalError("Stream Api Key is missing")
        }
        
        var config = ChatClientConfig(apiKey: .init(key))
        config.isLocalStorageEnabled = true
        config.applicationGroupIdentifier = "co.touchlab.droidconauthtest"

        // The resulting config is passed into a new `ChatClient` instance.
        let client = ChatClient(config: config)
        return client
    }()
    
    init() {
        log.info("ChatManager Created")
    }

    func connectUser(
        userData: UserData
    ) {
        log.info("Connecting to user")
        chatClient.connectUser(
            userInfo: .init(
                id: userData.id,
                name: userData.name,
                imageURL: userData.url
            ),
            token: .development(userId: userData.id) // TODO: Replace with Token from backend
        ) { error in
            if let error = error {
                // Some very basic error handling only logging the error.
                log.error("connecting the user failed \(error)")
                return
            } else {
                self.joinDefaultChannels(id: userData.id)
            }
        }
    }
    
    private func joinDefaultChannels(id: String) {
        log.debug("Joining the General Channel")

        // TODO: Make the Auto-Join Channel(s) configurable
        let controller = chatClient.channelController(for: .init(type: .messaging, id: "general"))
        controller.addMembers(
            userIds: [id],
            completion: { error in
                if let error = error {
                    // Some very basic error handling only logging the error.
                    log.error("adding the member failed \(error)")
                    return
                }
            }
        )
    }

}

private extension UserData {
    var url: URL? {
        if let pictureUrl {
            URL(string: pictureUrl)
        } else {
            nil
        }
    }
}
