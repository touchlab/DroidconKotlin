#!/usr/bin/python3

import json
import datetime
from shutil import copyfile
import os
import sys



filePath = "shared/src/commonMain/resources/schedule.json"
outputPath = filePath
# outputPathiOS = "iosApp/iosApp/schedule.json"
startDate = datetime.date.today()
customOutput = False
shouldPrint = False

# Gathering Arguments
if '-h' in sys.argv:
    print("arst")
    # print "usage: updateDates.py [option] ... [-i | -o | -d | -p] [arg] ..."
    # print "-i   : Input schedule file. Defaults to \"shared/src/commonMain/resources/schedule.json\""
    # print "-o   : Output schedule file. Defaults to input file."
    # print "-d   : Date to set as the first day of the conference, defaults to todays date"
    # print "-p   : Set to 1 to print the result to stdOut. If this is set no output files will be written"
else:
    argumentList = [sys.argv[i:i + 2] for i in range(1, len(sys.argv), 2)]
    for argPair in argumentList:
        argType = argPair[0]
        argValue = argPair[1]
        if argType ==   "-i":
            filePath = argValue
        elif argType == "-o":
            outputPath = argValue
            customOutput = True
        elif argType == "-d":
            startDate = datetime.datetime.strptime(argValue, "%m-%d-%Y")
        elif argType == "-p" and argValue == "1":
            shouldPrint = True

    if not customOutput:
        outputPath = filePath

    if os.path.isfile(filePath) == 0:
        print("Error: File not found at location:",filePath)
    else:

        # read existing file
        with open(filePath, "r") as f:
            data = json.load(f)

        # Update date
        todayDateTime = startDate
        tomorrowDateTime = startDate + datetime.timedelta(days=1)
        date1Str = todayDateTime.strftime("%Y-%m-%d")
        date2Str =  tomorrowDateTime.strftime("%Y-%m-%d")

        # Updating day 1 Dates
        for i in range(len(data)):
            day = data[i]

            dateStr = date1Str
            if i > 0:
                dateStr = date2Str

            data[i]["date"] = dateStr + "T" + "00:00:00"

            # Updating Session Dates
            for room in day["rooms"]:
                for session in room["sessions"]:

                    startingTime = session["startsAt"]
                    oldStartingTime = startingTime.split('T')
                    newStartTime = dateStr + 'T' + oldStartingTime[1]
                    session["startsAt"] = newStartTime

                    endingTime = session["endsAt"]
                    oldEndingTime = endingTime.split('T')
                    newEndTime = dateStr + 'T' + oldEndingTime[1]
                    session["endsAt"] = newEndTime

            #Updating timeslot dates
            for timeSlots in day["timeSlots"]:
                for room in timeSlots["rooms"]:
                    session = room['session']
                    if 'startsAt' in session:

                        startingTime = session["startsAt"]
                        oldStartingTime = startingTime.split('T')
                        newStartTime = dateStr + 'T' + oldStartingTime[1]
                        session["startsAt"] = newStartTime

                        endingTime = session["endsAt"]
                        oldEndingTime = endingTime.split('T')
                        newEndTime = dateStr + 'T' + oldEndingTime[1]
                        session["endsAt"] = newEndTime

                    room['session'] = session

            data[i] = day

        if shouldPrint:
            print(json.dumps(data))
        else:
            with open(outputPath, "w") as f:
                json.dump(data, f, indent=4)
