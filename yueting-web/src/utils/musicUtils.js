// 获取音乐时长
export const getMusicTime = (file) => {
	return new Promise((resolve, reject) => {
		const url = URL.createObjectURL(file);
		const audio = new Audio(url);
		// 监听元数据加载完成事件
		audio.onloadedmetadata = () => {
			URL.revokeObjectURL(url); // 释放内存
			resolve(audio.duration); // 返回时长（秒）
		};
		// 错误处理
		audio.onerror = (e) => {
			URL.revokeObjectURL(url);
			reject(new Error('解析音频时长失败'));
		};
	});
};