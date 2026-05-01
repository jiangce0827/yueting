/**
 * 格式化时长 (ms) 为 mm:ss 或 hh:mm:ss 格式
 * @param {number} ms 毫秒
 * @returns {string} 格式化后的字符串
 */
export function formatDuration(ms) {
	if (!ms || ms < 0) return '00:00';
	const totalSeconds = Math.floor(ms / 1000);
	const hours = Math.floor(totalSeconds / 3600);
	const minutes = Math.floor((totalSeconds % 3600) / 60);
	const seconds = totalSeconds % 60;

	if (hours > 0) {
		return `${hours}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
	}
	return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
}

/**
 * 格式化时长 (秒) 为 mm:ss 或 hh:mm:ss 格式
 * @param {number} seconds 秒
 * @returns {string} 格式化后的字符串
 */
export function formatSeconds(seconds) {
	if (!seconds || seconds < 0) return '00:00';
	const hours = Math.floor(seconds / 3600);
	const minutes = Math.floor((seconds % 3600) / 60);
	const secs = seconds % 60;

	if (hours > 0) {
		return `${hours}:${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
	}
	return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
}

/**
 * 格式化时间差为相对时间字符串
 * @param {string|Date} time 时间
 * @returns {string} 刚刚|N分钟前|N小时前|N天前|日期字符串
 */
export function formatRelativeTime(time) {
	if (!time) return '';
	const date = new Date(time);
	const now = new Date();
	const diff = now - date;
	if (diff < 60000) return '刚刚';
	if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
	if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
	if (diff < 604800000) return Math.floor(diff / 86400000) + '天前';
	return date.toLocaleDateString();
}
