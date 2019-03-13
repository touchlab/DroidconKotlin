#!/usr/bin/python3

import json
import datetime
from shutil import copyfile
import os
import sys



filePath = "sessionize/app/src/main/assets/"
startDate = datetime.date.today()

# Gathering Arguments
if '-h' in sys.argv:
    print "usage: updateDates.py [option] ... [-fp | -fn | -d] [arg] ..."
    print "-fp   : FilePath, defaults to \"sessionize/app/src/main/assets/schedule.json\""
    print "-d    : Date, defaults to todays date"
else:
    argumentList = [sys.argv[i:i + 2] for i in xrange(1, len(sys.argv), 2)]
    for argPair in argumentList:
        argType = argPair[0]
        argValue = argPair[1]
        if argType ==   "-fp":
            filePath = argValue
        elif argType == "-d":
            startDate = datetime.datetime.strptime(argValue, "%m-%d-%Y")


    if os.path.isfile(filePath) == 0:
        print "Error: File not found at location:",filePath
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


        print data
