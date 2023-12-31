[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3f9ba45b5c5449179150010659311f57)](https://www.codacy.com/manual/kai-morich/SimpleBluetoothLeTerminal?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kai-morich/SimpleBluetoothLeTerminal&amp;utm_campaign=Badge_Grade)

# Controller BLE Bandhayuda 2023 

Aplikasi Android yang berbasiskan Bluetooth Low Energy 

# ini Tugas Kita

PR: 
1) membuat layout baru di file xml baru untuk tampilan penentuan koordinat posisi robot,
mungkin pakai ini referensinya ini : https://youtu.be/8l4vsO3rldE (layout fragment_field.xml mulai dibuat)
+ javanya juga kalau tampilan udah selesai
2) mengganti orientation tampilan jadi landsacpe <sudah>
3) membuat tampilan controller joystick bisa pakai referensi
: https://www.akexorcist.com/2012/10/android-code-joystick-controller.html
+ javanya juga kalau tampilan udah selesai
4) Boleh di desain sebagus mungkin, aku gak terlalu tahu mengenai design UI yang baik, kreativitas kalian aja

YUK BISA YUK

![gambaran_aplikasi.png](./app/src/main/res/mipmap/gambaran_aplikasi.png)

# Penjelasan Project
1.) Modul Bluetooth : modul bluetooth yang sementara disambungkan ke komputer melalui USB-ttl untuk proses debugging
    - Cara pakai : langsung dipasang lalu melihat hasil melalui terminal Teraterm

![img_1.png](./app/src/main/res/mipmap/img_1.png)
    
2.) Teraterm : Terminal untuk menampilkan data dari modul Bluetooth LE untuk debug
    - Cara pakai: 
- buka teraterm dan Pasang ke Serial ke port yang tersedia / port yang disambungkan untuk modul bluetooth
![img.png](./app/src/main/res/mipmap/img.png)    

- Masuk menu Setting serial port

![img_2.png](./app/src/main/res/mipmap/img_2.png)

- Mengganti speed/baudrate ke 115200 karena modul bluetooth telah disetting ke baudrate 115200

![img_3.png](./app/src/main/res/mipmap/img_3.png)

3.) Tampilan aplikasi
- ini tampilan controller ketika mencari modul bluetooth yang aktif

![tampilan_1.jpeg](./app/src/main/res/mipmap/tampilan_1.jpeg)

- ini tampilan proses debug. 
      Aplikasi mengirim char 1, 2, 3, 4 yang merepresentasikan up, down, left, right. Modul bluetooth menerima lalu menampilkan dalam teraterm
      Bluetooth bisa mengirimkan char sembarang melalui input di teraterm. Aplikasi bisa menerima lalu menampilkan di fragmen terminal

![tampilan_2.jpeg](./app/src/main/res/mipmap/tampilan_2.jpeg)

# Referensi 
  https://github.com/kai-morich/SimpleBluetoothLeTerminal
#   c o n t r o l l e r _ b a n d h a y u d a  
 