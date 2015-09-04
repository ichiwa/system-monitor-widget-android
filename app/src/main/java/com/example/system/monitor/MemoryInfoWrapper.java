package com.example.system.monitor;

import java.io.RandomAccessFile;

public class MemoryInfoWrapper {

    private long memoryFree;        // 空のメモリ
    private long memoryInActive;    // アクティブじゃないメモリ
    private long memoryAvail;        // 使用されているメモリ
    private long memoryTotal;        // 使用出来る全体メモリ

    public MemoryInfoWrapper() {
        memoryTotal = 0;
        memoryFree = 0;
        memoryAvail = 0;
        memoryInActive = 0;
    }

    public void LoadMemory() {
        try {
            // meminfo から読み出し　まあ不正確ですけども。。。(´ω`；)
            RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r");
            String load = "";

            while ((load = reader.readLine()) != null) {
                // 使用出来るメモリサイズを取得
                if (load.startsWith("MemTotal:")) {
                    String[] toks = load.split(":");
                    this.memoryTotal = getStringToLong(toks[1]);
                    continue;
                }

                // MemFreeの取得
                if (load.startsWith("MemFree:")) {
                    String[] toks = load.split(":");
                    this.memoryFree = getStringToLong(toks[1]);
                    continue;
                }

                // Inactive Memの取得
                if (load.startsWith("Inactive:")) {
                    String[] toks = load.split(":");
                    this.memoryInActive = getStringToLong(toks[1]);
                    break;
                }
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 使用中メモリの計算
        this.memoryAvail = this.memoryTotal - (this.memoryInActive + this.memoryFree);

    }

    private long getStringToLong(String s) {
        s = s.replace(" ", "").replace("kB", "");
        return Long.parseLong(s);
    }

    public long getMemoryFree() {
        return memoryFree;
    }

    public void setMemoryFree(long memoryFree) {
        this.memoryFree = memoryFree;
    }

    public long getMemoryAvail() {
        return memoryAvail;
    }

    public void setMemoryAvail(long memoryAvail) {
        this.memoryAvail = memoryAvail;
    }

    public long getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(long memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public long getMemoryInactive() {
        return memoryInActive;
    }

    public void setMemoryInactive(long memoryInactive) {
        this.memoryInActive = memoryInactive;
    }

}
