const GEMINI_API_KEY = 'AIzaSyBsZ5IdvM6pLr6jdVgb3N-wKdBX0xiNzY4' 

const GEMINI_API_URL = 'https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent'

class GeminiChatbot {
    constructor() {
        this.isOpen = false;
        this.messages = [];
        this.isLoading = false;
        this.userContext = null; // Lưu context từ database
        this.init();
    }

    async init() {
        this.createChatWidget();
        this.bindEvents();
        await this.loadUserContext(); // Load context từ backend
        this.addWelcomeMessage();
    }

    // Lấy thông tin user từ database
    async loadUserContext() {
        try {
            const response = await fetch('/api/chatbot/context');
            if (response.ok) {
                this.userContext = await response.json();
                console.log('User context loaded:', this.userContext);
            }
        } catch (error) {
            console.error('Failed to load user context:', error);
            this.userContext = null;
        }
    }

    createChatWidget() {
        const chatHTML = `
            <div id="chatbot-container" class="fixed bottom-6 right-6 z-50 font-display">
                <!-- Chat Button -->
                <button id="chat-toggle-btn" class="w-14 h-14 bg-gradient-to-r from-primary to-blue-600 rounded-full shadow-lg flex items-center justify-center text-white hover:scale-110 transition-all duration-300 hover:shadow-xl">
                    <span class="material-symbols-outlined text-2xl" id="chat-icon">chat</span>
                </button>
                
                <!-- Chat Window -->
                <div id="chat-window" class="hidden absolute bottom-20 right-0 w-96 h-[500px] bg-white dark:bg-slate-800 rounded-2xl shadow-2xl border border-gray-200 dark:border-slate-700 flex flex-col overflow-hidden">
                    <!-- Header -->
                    <div class="bg-gradient-to-r from-primary to-blue-600 px-4 py-3 flex items-center gap-3">
                        <div class="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
                            <span class="material-symbols-outlined text-white">smart_toy</span>
                        </div>
                        <div class="flex-1">
                            <h3 class="text-white font-semibold text-sm">HR Assistant</h3>
                            <p class="text-white/70 text-xs">Powered by Gemini AI</p>
                        </div>
                        <button id="chat-close-btn" class="w-8 h-8 hover:bg-white/20 rounded-full flex items-center justify-center transition">
                            <span class="material-symbols-outlined text-white text-xl">close</span>
                        </button>
                    </div>
                    
                    <!-- Messages Area -->
                    <div id="chat-messages" class="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50 dark:bg-slate-900">
                        <!-- Messages will be inserted here -->
                    </div>
                    
                    <!-- Input Area -->
                    <div class="p-4 border-t border-gray-200 dark:border-slate-700 bg-white dark:bg-slate-800">
                        <form id="chat-form" class="flex items-center gap-2">
                            <input 
                                type="text" 
                                id="chat-input" 
                                placeholder="Nhập tin nhắn..." 
                                class="flex-1 px-4 py-2.5 border border-gray-200 dark:border-slate-600 rounded-xl bg-gray-50 dark:bg-slate-700 text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 dark:text-white"
                                autocomplete="off"
                            />
                            <button 
                                type="submit" 
                                id="chat-send-btn"
                                class="w-10 h-10 bg-primary hover:bg-primary/90 rounded-xl flex items-center justify-center text-white transition disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                <span class="material-symbols-outlined text-lg">send</span>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', chatHTML);
    }

    bindEvents() {
        const toggleBtn = document.getElementById('chat-toggle-btn');
        const closeBtn = document.getElementById('chat-close-btn');
        const chatForm = document.getElementById('chat-form');
        const chatInput = document.getElementById('chat-input');

        toggleBtn.addEventListener('click', () => this.toggleChat());
        closeBtn.addEventListener('click', () => this.closeChat());
        chatForm.addEventListener('submit', (e) => this.handleSubmit(e));
        
        // Enter to send
        chatInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                chatForm.dispatchEvent(new Event('submit'));
            }
        });
    }

    toggleChat() {
        this.isOpen = !this.isOpen;
        const chatWindow = document.getElementById('chat-window');
        const chatIcon = document.getElementById('chat-icon');
        
        if (this.isOpen) {
            chatWindow.classList.remove('hidden');
            chatWindow.classList.add('animate-slideUp');
            chatIcon.textContent = 'close';
            document.getElementById('chat-input').focus();
        } else {
            chatWindow.classList.add('hidden');
            chatIcon.textContent = 'chat';
        }
    }

    closeChat() {
        this.isOpen = false;
        document.getElementById('chat-window').classList.add('hidden');
        document.getElementById('chat-icon').textContent = 'chat';
    }

    addWelcomeMessage() {
        let welcomeMsg = "Xin chào! 👋 Tôi là HR Assistant, trợ lý ảo của hệ thống quản lý nhân sự.";
        
        // Cá nhân hóa lời chào nếu có thông tin user
        if (this.userContext && this.userContext.fullName) {
            welcomeMsg = `Xin chào ${this.userContext.fullName}! 👋 Tôi là HR Assistant.`;
        }
        
        welcomeMsg += "\n\nTôi có thể giúp bạn:\n• Tra cứu thông tin lương của bạn\n• Xem số ngày phép còn lại\n• Kiểm tra chấm công tháng này\n• Giải đáp chính sách công ty\n• Hướng dẫn sử dụng hệ thống\n\nHãy hỏi tôi bất cứ điều gì! 😊";
        
        this.addMessage('bot', welcomeMsg);
    }

    addMessage(type, content) {
        const messagesContainer = document.getElementById('chat-messages');
        const messageHTML = type === 'user' 
            ? this.createUserMessage(content)
            : this.createBotMessage(content);
        
        messagesContainer.insertAdjacentHTML('beforeend', messageHTML);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
        
        this.messages.push({ role: type === 'user' ? 'user' : 'model', content });
    }

    createUserMessage(content) {
        return `
            <div class="flex justify-end">
                <div class="max-w-[80%] bg-primary text-white px-4 py-2.5 rounded-2xl rounded-br-md text-sm">
                    ${this.escapeHtml(content)}
                </div>
            </div>
        `;
    }

    createBotMessage(content) {
        return `
            <div class="flex gap-2 items-start">
                <div class="w-8 h-8 bg-gradient-to-r from-primary to-blue-600 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                    <span class="material-symbols-outlined text-white text-sm">smart_toy</span>
                </div>
                <div class="max-w-[80%] bg-white dark:bg-slate-700 px-4 py-2.5 rounded-2xl rounded-tl-md text-sm text-gray-700 dark:text-gray-200 shadow-sm border border-gray-100 dark:border-slate-600 whitespace-pre-line leading-relaxed">
                    ${this.escapeHtml(content)}
                </div>
            </div>
        `;
    }

    showTypingIndicator() {
        const messagesContainer = document.getElementById('chat-messages');
        const typingHTML = `
            <div id="typing-indicator" class="flex gap-2">
                <div class="w-8 h-8 bg-gradient-to-r from-primary to-blue-600 rounded-full flex items-center justify-center flex-shrink-0">
                    <span class="material-symbols-outlined text-white text-sm">smart_toy</span>
                </div>
                <div class="bg-white dark:bg-slate-700 px-4 py-3 rounded-2xl rounded-tl-md shadow-sm border border-gray-100 dark:border-slate-600">
                    <div class="flex gap-1">
                        <span class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0ms"></span>
                        <span class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 150ms"></span>
                        <span class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 300ms"></span>
                    </div>
                </div>
            </div>
        `;
        messagesContainer.insertAdjacentHTML('beforeend', typingHTML);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    hideTypingIndicator() {
        const indicator = document.getElementById('typing-indicator');
        if (indicator) indicator.remove();
    }

    async handleSubmit(e) {
        e.preventDefault();
        
        const input = document.getElementById('chat-input');
        const sendBtn = document.getElementById('chat-send-btn');
        const message = input.value.trim();
        
        if (!message || this.isLoading) return;
        
        // Add user message
        this.addMessage('user', message);
        input.value = '';
        
        // Disable input while loading
        this.isLoading = true;
        sendBtn.disabled = true;
        input.disabled = true;
        
        // Show typing indicator
        this.showTypingIndicator();
        
        try {
            const response = await this.sendToGemini(message);
            this.hideTypingIndicator();
            this.addMessage('bot', response);
        } catch (error) {
            this.hideTypingIndicator();
            this.addMessage('bot', 'Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại sau! 😔');
            console.error('Gemini API Error:', error);
        } finally {
            this.isLoading = false;
            sendBtn.disabled = false;
            input.disabled = false;
            input.focus();
        }
    }

    async sendToGemini(message) {
        // Xây dựng context từ thông tin database
        let userContextStr = '';
        if (this.userContext && !this.userContext.error) {
            userContextStr = this.buildContextString();
        }
        
        const systemPrompt = `Bạn là HR Assistant - trợ lý ảo thông minh của hệ thống quản lý nhân sự (HR Management System).

Nhiệm vụ của bạn:
- Hỗ trợ nhân viên tra cứu thông tin về lương, nghỉ phép, chấm công
- Giải đáp các câu hỏi về chính sách nhân sự, quy định công ty
- Hướng dẫn sử dụng các chức năng của hệ thống
- Trả lời thân thiện, chuyên nghiệp bằng tiếng Việt
- Sử dụng emoji phù hợp để tạo cảm giác thân thiện

Lưu ý quan trọng:
- Trả lời ngắn gọn, súc tích, dễ hiểu
- Khi user hỏi về lương, phép, chấm công của họ -> sử dụng thông tin từ CONTEXT bên dưới
- Không tiết lộ thông tin nhạy cảm về lương của người khác
- Nếu không có thông tin trong context, đề nghị user liên hệ phòng nhân sự

${userContextStr}`;

        const requestBody = {
            contents: [
                {
                    role: 'user',
                    parts: [{ text: systemPrompt + '\n\nCâu hỏi của nhân viên: ' + message }]
                }
            ],
            generationConfig: {
                temperature: 0.7,
                topK: 40,
                topP: 0.95,
                maxOutputTokens: 1024,
            }
        };

        const response = await fetch(`${GEMINI_API_URL}?key=${GEMINI_API_KEY}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            throw new Error(`API request failed: ${response.status}`);
        }

        const data = await response.json();
        
        if (data.candidates && data.candidates[0] && data.candidates[0].content) {
            return data.candidates[0].content.parts[0].text;
        }
        
        throw new Error('Invalid response format');
    }

    // Xây dựng chuỗi context từ dữ liệu database
    buildContextString() {
        const ctx = this.userContext;
        let str = '\n=== THÔNG TIN NHÂN VIÊN ĐANG ĐĂNG NHẬP (TỪ DATABASE) ===\n';
        
        str += `Ngày hiện tại: ${ctx.currentDate}\n`;
        str += `Họ tên: ${ctx.fullName}\n`;
        str += `Email: ${ctx.email}\n`;
        str += `Vai trò: ${ctx.role}\n`;
        
        if (ctx.employee) {
            const emp = ctx.employee;
            str += `\n--- Thông tin nhân viên ---\n`;
            str += `Mã NV: ${emp.MNV || 'Chưa có'}\n`;
            str += `Phòng ban: ${emp.department || 'Chưa có'}\n`;
            str += `Chức vụ: ${emp.position || 'Chưa có'}\n`;
            str += `SĐT: ${emp.phone || 'Chưa có'}\n`;
            str += `Địa chỉ: ${emp.address || 'Chưa có'}\n`;
            str += `Ngày sinh: ${emp.dateOfBirth || 'Chưa có'}\n`;
            str += `Ngày vào công ty: ${emp.joinDate || 'Chưa có'}\n`;
            str += `Lương cơ bản: ${emp.basicSalary || 'Chưa có'}\n`;
        }
        
        if (ctx.salaryHistory && ctx.salaryHistory.length > 0) {
            str += `\n--- Lịch sử lương gần đây ---\n`;
            ctx.salaryHistory.forEach(sal => {
                str += `Tháng ${sal.month}/${sal.year}: Lương cơ bản ${sal.basicSalary}, Phụ cấp ${sal.allowance}, Thưởng ${sal.bonus}, Khấu trừ ${sal.deduction}, TỔNG ${sal.totalSalary}, Ngày công: ${sal.workDays}, Tăng ca: ${sal.overtimeHours}h\n`;
            });
        } else {
            str += `\n--- Chưa có dữ liệu lương ---\n`;
        }
        
        if (ctx.leaveInfo) {
            str += `\n--- Thông tin nghỉ phép ---\n`;
            str += `Số ngày phép đã sử dụng trong năm: ${ctx.leaveInfo.totalLeaveDaysUsed} ngày\n`;
            str += `Số ngày phép còn lại: ${ctx.leaveInfo.remainingDays} ngày (giả sử 12 ngày/năm)\n`;
            str += `Đơn xin phép đang chờ duyệt: ${ctx.leaveInfo.pendingRequests} đơn\n`;
            str += `Đơn đã được duyệt: ${ctx.leaveInfo.approvedRequests} đơn\n`;
        }
        
        if (ctx.attendanceInfo) {
            str += `\n--- Chấm công tháng ${ctx.attendanceInfo.currentMonth}/${ctx.attendanceInfo.currentYear} ---\n`;
            str += `Số ngày đã chấm công: ${ctx.attendanceInfo.workDaysThisMonth} ngày\n`;
            str += `Tổng giờ tăng ca: ${ctx.attendanceInfo.overtimeHoursThisMonth} giờ\n`;
            
            if (ctx.attendanceInfo.checkedInToday) {
                str += `Hôm nay đã check-in: ${ctx.attendanceInfo.checkInTime || 'Có'}\n`;
                str += `Check-out: ${ctx.attendanceInfo.checkOutTime || 'Chưa'}\n`;
            } else {
                str += `Hôm nay chưa check-in\n`;
            }
        }
        
        str += '=== HẾT CONTEXT ===\n';
        return str;
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Initialize chatbot when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.hrChatbot = new GeminiChatbot();
});
