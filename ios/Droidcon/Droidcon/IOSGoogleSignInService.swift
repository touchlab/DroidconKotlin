//
//  FirebaseService.swift
//  Droidcon
//
//  Created by Kevin Schildhorn on 3/27/24.
//  Copyright © 2024 Touchlab. All rights reserved.
//

import Foundation
import FirebaseCore
import GoogleSignIn
import FirebaseAuth
import DroidconKit

class IOSGoogleSignInService : GoogleSignInService {

    let logger = Logger.companion.withTag(tag: "IOSGoogleSignInService")
    
    func performGoogleLogin() -> Bool {
        logger.i(message: { "Performing Google Login" })
        guard let clientID = FirebaseApp.app()?.options.clientID else {
            logger.e(message: { "No ClientId" })
            return false
        }

        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config
        guard let presentingViewController = UIApplication.shared.windows.first?.rootViewController
        else {
            logger.e(message: { "No Presenting Controller" })
            return false
        }
        
        logger.v(message: { "Sign In with Shared Google Instance" })
        GIDSignIn.sharedInstance.signIn(withPresenting: presentingViewController) { result, error in
            guard error == nil else {
                self.logger.e(message: { error?.localizedDescription ?? "" })
                return
            }

            guard let user = result?.user,
                  let idToken = user.idToken?.tokenString
            else {
                self.logger.e(message: { "No User Found" })
                return
            }
            
            self.logger.v(message: { "Get Credentials from Auth Provider" })
            let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: user.accessToken.tokenString)
            Auth.auth().signIn(with: credential) { result, error in
                if let error {
                    self.logger.e(message: { error.localizedDescription })
                } else {
                    self.logger.v(message: { "Got results from Auth!" })
                }
            }
        }
        return true
    }
    
    func performGoogleLogout() -> Bool {
        do {
            self.logger.v(message: { "Performing Logout" })
            try Auth.auth().signOut()
            return true
        } catch let signOutError as NSError {
            self.logger.e(message: { "Error occured signing out: \(signOutError.localizedDescription)" })
            return false
        }
    }
}
