import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

// WebSocket 服务类
class MessageWebSocket {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.messageCallback = null;
    this.connectCallback = null;
    this.disconnectCallback = null;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectDelay = 3000;
  }

  // 连接到 WebSocket 服务器
  connect(userId, token) {
    if (this.stompClient && this.connected) {
      console.log('[WebSocket] Already connected');
      return;
    }

    // 使用 SockJS 连接到后端（通过 Vite 代理）
    const socket = new SockJS(`/ws?token=${token}`);

    this.stompClient = new Client({
      webSocketFactory: () => socket,
      // 关闭调试日志
      debug: () => {},
      // 连接成功回调
      onConnect: (frame) => {
        console.log('[WebSocket] Connected:', frame);
        this.connected = true;
        this.reconnectAttempts = 0;

        // 订阅用户私信队列
        this.subscribeToUserMessages(userId);

        // 执行连接成功回调
        if (this.connectCallback) {
          this.connectCallback();
        }
      },
      // 连接错误回调
      onStompError: (frame) => {
        console.error('[WebSocket] STOMP error:', frame);
        this.connected = false;
        this.handleReconnect(userId, token);
      },
      // 断开连接回调
      onDisconnect: (frame) => {
        console.log('[WebSocket] Disconnected:', frame);
        this.connected = false;
        if (this.disconnectCallback) {
          this.disconnectCallback();
        }
      },
    });

    // 激活连接
    this.stompClient.activate();
  }

  // 订阅用户私信队列
  subscribeToUserMessages(userId) {
    if (!this.stompClient || !this.connected) {
      console.warn('[WebSocket] Cannot subscribe: not connected');
      return;
    }

    const subscription = this.stompClient.subscribe(
      `/topic/private-message.${userId}`,
      (message) => {
        if (this.messageCallback) {
          const body = JSON.parse(message.body);
          console.log('[WebSocket] Received message:', body);
          this.messageCallback(body);
        }
      }
    );

    console.log('[WebSocket] Subscribed to /topic/private-message.' + userId);
    return subscription;
  }

  // 发送私信（通过 WebSocket）
  sendMessage(receiverId, content) {
    if (!this.stompClient || !this.connected) {
      console.warn('[WebSocket] Cannot send message: not connected');
      return false;
    }

    this.stompClient.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({
        receiverId: receiverId,
        content: content
      })
    });
    return true;
  }

  // 标记消息已读
  markAsRead(senderId) {
    if (!this.stompClient || !this.connected) {
      return false;
    }

    this.stompClient.publish({
      destination: '/app/chat.markRead',
      body: JSON.stringify({
        senderId: senderId
      })
    });
    return true;
  }

  // 处理断线重连
  handleReconnect(userId, token) {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('[WebSocket] Max reconnection attempts reached');
      return;
    }

    this.reconnectAttempts++;
    console.log(`[WebSocket] Reconnecting... Attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);

    setTimeout(() => {
      this.connect(userId, token);
    }, this.reconnectDelay);
  }

  // 断开连接
  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.stompClient = null;
      this.connected = false;
      console.log('[WebSocket] Disconnected manually');
    }
  }

  // 设置消息回调
  onMessage(callback) {
    this.messageCallback = callback;
  }

  // 设置连接成功回调
  onConnect(callback) {
    this.connectCallback = callback;
  }

  // 设置断开连接回调
  onDisconnect(callback) {
    this.disconnectCallback = callback;
  }

  // 检查是否已连接
  isConnected() {
    return this.connected;
  }
}

// 导出单例
export const messageWebSocket = new MessageWebSocket();
