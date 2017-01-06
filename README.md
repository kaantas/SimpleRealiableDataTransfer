# SIMPLE JAVA PROGRAM ABOUT IP FRAGMENTATION-REASSEMBLY & REALIABLE DATA TRANSFER
1-Önce Receiver sonra Sender çalıştırıyoruz.

2-1024 byte'lık paketin ilk 2 byte'ı(16bit) Paket Numarası olarak kullandık.

3-3.byte ise flag olarak belirtip son mesajı tespit etmek için kullandık.

4-Geri kalan 1021 byte = Pure Data

5-Datagram paket şeklinde bu mesajı Receiver'a yolluyoruz.

6-Sıradaki paketi yollamak için Receiver'dan ACK bekliyoruz.

7-Receiver paketi aldığında ilk 3 byte'a bakıp mesajın kaçıncı pakete dahil olduğunu ve son paket
olup olmadığını tespit ediyor.

8-Receiver Sender'a en son kaçıncı paket geldiğini ACK olarak yolluyor.

9-Sender Receiver'dan gelen ACK ile gönderdiği paket numarasını karşılaştırıyor.

10-Eğer eşitse doğru ACK geldiğini anlayıp sıradaki paketi yolluyor.

11-Eğer eşit değilse aynı paketi bir daha yolluyor.

12-En son ise kalan paket yollanıyor. Son paket 3.byte'ımız olan flag ile tespit ediliyor.

