/*
 Copyright 2018-present the Material Components for iOS authors. All Rights Reserved.
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import UIKit

import MaterialComponents

class ApplicationScheme: NSObject {
    
    private static var singleton = ApplicationScheme()
    
    static var shared: ApplicationScheme {
        return singleton
    }
    
    override init() {
        self.buttonScheme.colorScheme = self.colorScheme
        self.buttonScheme.typographyScheme = self.typographyScheme
        super.init()
    }
    
    public let buttonScheme = MDCButtonScheme()
    
    public let rsvpColor = colorWithHexString(hexString: "68d1fd")
    public let rsvpColorConflict = colorWithHexString(hexString: "f1582c")
    public let rsvpColorPast = colorWithHexString(hexString: "666666")
    
    public let colorScheme: MDCColorScheming = {
        let scheme = MDCSemanticColorScheme(defaults: .material201804)
        
        scheme.primaryColor = colorWithHexString(hexString: "1e2981")
        scheme.primaryColorVariant = colorWithHexString(hexString: "233096")
        scheme.onPrimaryColor = colorWithHexString(hexString: "ffffff")
        scheme.secondaryColor = colorWithHexString(hexString: "3f4caf")
        scheme.onSecondaryColor = colorWithHexString(hexString: "ffffff")
        scheme.surfaceColor = colorWithHexString(hexString: "ffffff")
        scheme.onSurfaceColor = colorWithHexString(hexString: "000000")
        scheme.backgroundColor = colorWithHexString(hexString: "ffffff")
        scheme.onBackgroundColor = colorWithHexString(hexString: "000000")
        scheme.errorColor = colorWithHexString(hexString: "C5032B")
        
        return scheme
    }()
    
    public let menuColorScheme: MDCColorScheming = {
        let scheme = MDCSemanticColorScheme(defaults: .material201804)
        
        scheme.primaryColor = colorWithHexString(hexString: "233096")
        scheme.primaryColorVariant = colorWithHexString(hexString: "1e2981")
        scheme.onPrimaryColor = colorWithHexString(hexString: "f4f04f")
        scheme.secondaryColor = colorWithHexString(hexString: "3f4caf")
        scheme.onSecondaryColor = colorWithHexString(hexString: "ffffff")
        scheme.surfaceColor = colorWithHexString(hexString: "ffffff")
        scheme.onSurfaceColor = colorWithHexString(hexString: "000000")
        scheme.backgroundColor = colorWithHexString(hexString: "ffffff")
        scheme.onBackgroundColor = colorWithHexString(hexString: "000000")
        scheme.errorColor = colorWithHexString(hexString: "C5032B")
        
        return scheme
    }()
    
    public let typographyScheme: MDCTypographyScheming = {
        let scheme = MDCTypographyScheme()
        //TODO: Add our custom fonts after this line
        
        return scheme
    }()
    
   
}

func colorWithHexString(hexString: String, alpha:CGFloat? = 1.0) -> UIColor {
    
    // Convert hex string to an integer
    let hexint = Int(intFromHexString(hexStr: hexString))
    let red = CGFloat((hexint & 0xff0000) >> 16) / 255.0
    let green = CGFloat((hexint & 0xff00) >> 8) / 255.0
    let blue = CGFloat((hexint & 0xff) >> 0) / 255.0
    let alpha = alpha!
    
    // Create color object, specifying alpha as well
    let color = UIColor(red: red, green: green, blue: blue, alpha: alpha)
    return color
}

func intFromHexString(hexStr: String) -> UInt32 {
    var hexInt: UInt32 = 0
    // Create scanner
    let scanner: Scanner = Scanner(string: hexStr)
    // Tell scanner to skip the # character
    scanner.charactersToBeSkipped = CharacterSet(charactersIn: "#")
    // Scan hex value
    scanner.scanHexInt32(&hexInt)
    return hexInt
}
