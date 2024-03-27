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

class FirebaseService {
    
    func signIn(onError: @escaping (String) -> Void) {
        guard let clientID = FirebaseApp.app()?.options.clientID else {
            onError("No ClientId")
            return
        }

        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config
        guard let presentingViewController = UIApplication.shared.windows.first?.rootViewController
        else {
            onError("No Presenting Controller")
            return
        }
        
        GIDSignIn.sharedInstance.signIn(withPresenting: presentingViewController) { result, error in
            guard error == nil else {
                onError(error?.localizedDescription ?? "")
                return
            }

            guard let user = result?.user,
                  let idToken = user.idToken?.tokenString
            else {
                onError("No User Found")
                return
            }

            let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: user.accessToken.tokenString)
            Auth.auth().signIn(with: credential) { result, error in
                if let error {
                    onError(error.localizedDescription)
                }
            }
        }
    }
    
    func signOut() -> String? {
        do {
          try Auth.auth().signOut()
            return nil
        } catch let signOutError as NSError {
            return signOutError.localizedDescription
        }
    }
}
