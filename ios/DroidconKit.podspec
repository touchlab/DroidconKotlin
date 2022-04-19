# Podspec modified by Faktory
# Further manual changes will be overwritten

require 'zip'

local = File.exist?(ENV["HOME"] + "/.kmmlocal") || !File.exist?(".faktory/url")
puts "local=#{local}"

Pod::Spec.new do |spec|
    spec.name                     = 'DroidconKit'
    spec.version                  = '1.0'
    spec.homepage                 = 'https://github.com/touchlab/DroidconKotlin'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Common library for the Droidcon app'
    spec.libraries                = 'c++'
                
                
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':DroidconKit',
        'PRODUCT_MODULE_NAME' => 'DroidconKit',
    }
                
    if (local)
        spec.vendored_frameworks      = 'build/cocoapods/framework/DroidconKit.framework'
        spec.script_phases = [
            {
                :name => 'Build DroidconKit',
                :execution_position => :before_compile,
                :shell_path => '/bin/sh',
                :script => <<-SCRIPT
                    if [ "YES" = "$COCOAPODS_SKIP_KOTLIN_BUILD" ]; then
                      echo "Skipping Gradle build task invocation due to COCOAPODS_SKIP_KOTLIN_BUILD environment variable set to \"YES\""
                      exit 0
                    fi
                    set -ev
                    REPO_ROOT="$PODS_TARGET_SRCROOT"
                    "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                        -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                        -Pkotlin.native.cocoapods.archs="$ARCHS" \
                        -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
                SCRIPT
            }
        ]
    else
        puts 'Downloading remote pod'
        FileUtils.rm_rf './build/faktory/remote-download'
        FileUtils.mkdir_p './build/faktory/remote-download'
        
        urlFile = File.open(".faktory/url")
        download = URI.open(urlFile.read)
        Zip::File.open(download) do |zip_file|
            zip_file.each do |f|
                fpath = "./build/faktory/remote-download/#{f.name}"
                zip_file.extract(f, fpath)
                if fpath.end_with?(".plist")
                    data = File.read(fpath) 
                    filtered_data = data.gsub("/notreal/replaceme", "#{Dir.pwd}") 
                    # open the file for writing
                    File.open(fpath, "w") do |f|
                      f.write(filtered_data)
                    end
                end
            end
        end
        
        spec.vendored_frameworks = "build/faktory/remote-download/DroidconKit.xcframework"
    end
                
end