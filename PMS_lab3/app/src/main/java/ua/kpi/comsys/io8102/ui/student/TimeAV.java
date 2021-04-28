package ua.kpi.comsys.io8102.ui.student;


import androidx.annotation.NonNull;
import java.util.Date;


public class TimeAV {
    int hours;
    int minutes;
    int seconds;

    public TimeAV() {
        setTime(0, 0, 0);
    }

    public TimeAV(int hours, int minutes, int seconds) {
        if ((hours >= 0 && hours <=23) && (minutes >= 0 && minutes <= 59) &&
                (seconds >= 0 && seconds <= 59)) {
            setTime(hours, minutes, seconds);
        } else {
            System.out.println("TimeAV was not created");
        }
    }

    public TimeAV(Date date) {
        this.hours = date.getHours();
        this.minutes = date.getMinutes();
        this.seconds = date.getSeconds();
    }


    public TimeAV addTime(TimeAV time) {
        TimeAV resultTime = new TimeAV(hours, minutes, seconds);

        if (resultTime.hours + time.hours >= 24)
            resultTime.hours = (resultTime.hours + time.hours) % 24;
        else
            resultTime.hours += time.hours;

        if (resultTime.minutes + time.minutes >= 60) {
            resultTime.minutes = (resultTime.minutes + time.minutes) % 60;
            if (resultTime.hours != 23)
                resultTime.hours ++;
            else
                resultTime.hours = 0;
        } else resultTime.minutes += time.minutes;

        if (resultTime.seconds + time.seconds >= 60) {
            resultTime.seconds = (resultTime.seconds + time.seconds) % 60;
            if (resultTime.minutes != 59)
                resultTime.minutes ++;
            else
                resultTime.minutes = 0;
            if (resultTime.hours != 23)
                resultTime.hours ++;
            else
                resultTime.hours = 0;
        } else resultTime.seconds += time.seconds;

        return resultTime;
    }


    public TimeAV subtractTime(TimeAV time) {
        TimeAV resultTime = new TimeAV(hours, minutes, seconds);

        if (resultTime.hours >= time.hours)
            resultTime.hours -= time.hours;
        else {
            if (time.hours < 24)
                resultTime.hours = 24 - (time.hours - resultTime.hours);
            else if (time.hours > 24)
                resultTime.hours = 24 - ((time.hours - resultTime.hours) % 24);
        }

        if (resultTime.minutes >= time.minutes) {
            resultTime.minutes -= time.minutes;
        } else {
            if (time.minutes < 60)
                resultTime.minutes = 60 - (time.minutes - resultTime.minutes);
            else if (time.minutes > 60)
                resultTime.minutes = 60 - ((time.minutes - resultTime.minutes) % 60);
            if (resultTime.hours != 0)
                resultTime.hours --;
            else
                resultTime.hours = 23;
        }

        if (resultTime.seconds >= time.seconds) {
            resultTime.seconds -= time.seconds;
        } else {
            if (time.seconds < 60)
                resultTime.seconds = 60 - (time.seconds - resultTime.seconds);
            else if (time.seconds > 60)
                resultTime.seconds = 60 - ((time.seconds - resultTime.seconds) % 60);
            if (resultTime.minutes != 0)
                resultTime.minutes --;
            else {
                resultTime.minutes = 59;
                if (resultTime.hours != 0)
                    resultTime.hours--;
                else
                    resultTime.hours = 23;
            }
        }

        return resultTime;
    }

    public static TimeAV addTwo(TimeAV timeOne, TimeAV timeTwo) {
        TimeAV resultTime = new TimeAV(timeOne.hours, timeOne.minutes, timeOne.seconds);

        if (timeOne.hours + timeTwo.hours >= 24)
            resultTime.hours = (timeOne.hours + timeTwo.hours) % 24;
        else
            resultTime.hours = timeOne.hours + timeTwo.hours;

        if (timeOne.minutes + timeTwo.minutes >= 60) {
            resultTime.minutes = (timeOne.minutes + timeTwo.minutes) % 60;
            if (resultTime.hours != 23)
                resultTime.hours ++;
            else
                resultTime.hours = 0;
        } else resultTime.minutes = timeOne.minutes + timeTwo.minutes;

        if (timeOne.seconds + timeTwo.seconds >= 60) {
            resultTime.seconds = (timeOne.seconds + timeTwo.seconds) % 60;
            if (resultTime.minutes != 59)
                resultTime.minutes ++;
            else
                resultTime.minutes = 0;
            if (resultTime.hours != 23)
                resultTime.hours ++;
            else
                resultTime.hours = 0;
        } else resultTime.seconds = timeOne.seconds + timeTwo.seconds;

        return resultTime;
    }


    public static TimeAV subtractTwo(TimeAV timeOne, TimeAV timeTwo) {
        TimeAV resultTime = new TimeAV(timeOne.hours, timeOne.minutes, timeOne.seconds);

        if (timeOne.hours >= timeTwo.hours)
            resultTime.hours = timeOne.hours - timeTwo.hours;
        else {
            if (timeTwo.hours < 24)
                resultTime.hours = 24 - (timeTwo.hours - timeOne.hours);
            else if (timeTwo.hours > 24)
                resultTime.hours = 24 - ((timeTwo.hours - timeOne.hours) % 24);
        }

        if (timeOne.minutes >= timeTwo.minutes) {
            resultTime.minutes = timeOne.minutes - timeTwo.minutes;
        } else {
            if (timeTwo.minutes < 60)
                resultTime.minutes = 60 - (timeTwo.minutes - timeOne.minutes);
            else if (timeTwo.minutes > 60)
                resultTime.minutes = 60 - ((timeTwo.minutes - timeOne.minutes) % 60);
            if (resultTime.hours != 0)
                resultTime.hours --;
            else
                resultTime.hours = 23;
        }

        if (timeOne.seconds >= timeTwo.seconds) {
            resultTime.seconds = timeOne.seconds - timeTwo.seconds;
        } else {
            if (timeTwo.seconds > 60)
                resultTime.seconds = 60 - ((timeTwo.seconds - timeOne.seconds) % 60);
            else if (timeTwo.seconds < 60)
                resultTime.seconds = 60 - (timeTwo.seconds - timeOne.seconds);
            if (resultTime.minutes != 0)
                resultTime.minutes --;
            else {
                resultTime.minutes = 59;
                if (resultTime.hours != 0)
                    resultTime.hours--;
                else
                    resultTime.hours = 23;
            }
        }

        return resultTime;
    }


    @NonNull
    @Override
    public String toString() {
        String hours = "";
        String minutes = "";
        String seconds = "";
        String partOfTheDay;

        if (this.hours >= 12) {
            if (this.hours - 12 < 10 && this.hours != 12) {
                hours += "0";
                hours += this.hours - 12;
            } else if (this.hours == 12) {
                hours += 12;
            } else {
                hours += this.hours - 12;
            }
            partOfTheDay = "PM";
        } else {
            if (this.hours < 10 && this.hours != 0) {
                hours += "0";
                hours += this.hours;
            } else if (this.hours == 0) {
                hours += 12;
            } else {
                hours += this.hours;
            }
            partOfTheDay = "AM";
        }

        if (this.minutes > 9){
           minutes = String.valueOf(this.minutes);
        } else {
           minutes = ("0" + this.minutes);
        }

        if (this.seconds > 9){
            seconds = String.valueOf(this.seconds);
        } else {
            seconds = ("0" + this.seconds);
        }

        return hours + ":" + minutes + ":" + seconds + " " + partOfTheDay;
    }


    private void setTime(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }
}