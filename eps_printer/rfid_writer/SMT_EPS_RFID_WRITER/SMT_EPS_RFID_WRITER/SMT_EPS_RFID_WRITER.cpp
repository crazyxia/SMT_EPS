#include "stdafx.h"
#include <stdlib.h>
#include<windows.h>
#include<string>
//#pragma comment( linker, "/subsystem:\"windows\" /entry:\"mainCRTStartup\"" )
using namespace std;

extern "C" {
	#include "ACIdealRfidSdk.h"
	#include <sys/types.h>
	#include <winsock.h>
}

//socketID
int sockfd;
//serverID
int serverfd;
//连接信息
struct sockaddr_in my_addr;
struct sockaddr_in their_addr;



int main()
{
	//初始化socket监听
	WSADATA wsaData;
	WSAStartup(MAKEWORD(2, 2), &wsaData);
	my_addr.sin_family = AF_INET;
	my_addr.sin_port = htons(10102);
	my_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	memset(&(my_addr.sin_zero), '\0',8);
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	bind(sockfd, (struct sockaddr *)&my_addr, sizeof(SOCKADDR_IN));
	listen(sockfd, 20);
	printf("监听10102端口连接...\n");
	//监听连接进入
	int addrlen = sizeof(SOCKADDR_IN);
	serverfd = accept(sockfd, (struct sockaddr*)&their_addr, &addrlen);
	if (serverfd != -1) {
		//初始化RFID
		if (ACIDEAL_RFID_Init() == 0) {
			//反馈成功信号
			printf("读写器初始化成功\n");
			send(serverfd, "1", 1, 0);
			//进入写循环
			printf("等待任务到达...\n");
			while (1) {
				//接收1个字节，作为工作标志，如果是1就表示在工作，如果是0则表示已经关闭
				char flag[1] = { 0 };
				recv(serverfd, flag, 1, 0);
				if (flag[0] == '0' || flag[0] == '\0') {
					printf("远程连接已关闭\n");
					break;
				}
				//接收126个字节
				char data[126] = {0};
				recv(serverfd, data, 126, 0);
				//写数据到RFID中
				if (ACIDEAL_RFID_WriteEPC(data, 126) == 0) {
					//反馈成功信号
					printf("写入数据成功\n");
					send(serverfd, "1", 1, 0);
					ACIDEAL_RFID_Beep_Shine_On();
					Sleep(50);
					ACIDEAL_RFID_Beep_Shine_Off();
					Sleep(50);
					ACIDEAL_RFID_Beep_Shine_On();
					Sleep(50);
					ACIDEAL_RFID_Beep_Shine_Off();
				}
				else {
					//反馈成功信号
					printf("写入数据失败\n");
					send(serverfd, "0", 1, 0);
					ACIDEAL_RFID_Beep_Shine_On();
					Sleep(500);
					ACIDEAL_RFID_Beep_Shine_Off();
				}
			}
		}
		else {
			printf("读写器初始化失败\n");
			send(serverfd, "0", 1, 0);
		}
	}
	closesocket(sockfd);
	closesocket(serverfd);
	WSACleanup();
	printf("程序已结束\n");
	system("pause");
    return 0;
}

