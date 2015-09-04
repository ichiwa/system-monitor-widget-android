package com.example.system.monitor;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CpuInfoWrapper {

    private float cpuavail = 0;

    public CpuInfoWrapper() {

    }

    public void LoadCpuInfo() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            this.cpuavail = (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

            reader.close();

            return;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.cpuavail = 0;
    }

    public float getCpuavail() {
        return cpuavail;
    }

    public void setCpuavail(float cpuavail) {
        this.cpuavail = cpuavail;
    }

}
