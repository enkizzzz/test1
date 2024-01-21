package com.example.test;

public class Orders {
    private int WaitNumber;
    private int day;
    private Time time; // Time 객체에 대한 참조

    // Time 내부 클래스
    public static class Time {
        private int hour;
        private int minute;
        private int second;

        // Time 클래스의 getter와 setter 메서드들
        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }
    }

    // Orders 클래스의 기본 생성자
    public Orders() { }

    // Orders 클래스의 getter와 setter 메서드들
    public int getWaitNumber() {
        return WaitNumber;
    }

    public void setWaitNumber(int WaitNumber) {
        this.WaitNumber = WaitNumber;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    // Time 객체에 대한 getter와 setter (Orders 클래스 내부에 정의되어야 함)
    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}