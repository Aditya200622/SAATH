// SAATH Nudge & Notification Manager
// Controls smart prompts, quiet hours, daily caps, and deduplication

export const NUDGE_TYPES = {
  MORNING_CHECKIN: 'MORNING_CHECKIN',
  TASK_REMINDER: 'TASK_REMINDER',
  FOCUS_REMINDER: 'FOCUS_REMINDER',
  EVENING_REFLECTION: 'EVENING_REFLECTION'
};

const DEFAULT_SETTINGS = {
  enabled: true,
  quietHoursEnabled: true,
  quietHoursStart: 22, // 10 PM
  quietHoursEnd: 7,    // 7 AM
  maxDailyNudges: 4
};

export class NudgeManager {
  static getSettings() {
    try {
      const saved = localStorage.getItem('saath_nudge_settings');
      if (saved) return JSON.parse(saved);
    } catch (e) {}
    return DEFAULT_SETTINGS;
  }

  static saveSettings(settings) {
    try {
      localStorage.setItem('saath_nudge_settings', JSON.stringify(settings));
    } catch (e) {}
  }

  static getSentHistory() {
    try {
      const saved = localStorage.getItem('saath_nudge_history');
      if (saved) return JSON.parse(saved);
    } catch (e) {}
    return [];
  }

  static isQuietHours(settings) {
    if (!settings.quietHoursEnabled) return false;
    const currentHour = new Date().getHours();
    if (settings.quietHoursStart > settings.quietHoursEnd) {
      // e.g. 22:00 to 07:00
      return currentHour >= settings.quietHoursStart || currentHour < settings.quietHoursEnd;
    }
    return currentHour >= settings.quietHoursStart && currentHour < settings.quietHoursEnd;
  }

  static canTriggerNudge(type, settings = null) {
    const s = settings || this.getSettings();
    if (!s.enabled) return { allowed: false, reason: 'Notifications disabled' };

    if (this.isQuietHours(s)) {
      return { allowed: false, reason: 'Quiet hours active (10:00 PM - 07:00 AM)' };
    }

    const todayStr = new Date().toISOString().split('T')[0];
    const history = this.getSentHistory();
    const todayNudges = history.filter(h => h.date === todayStr);

    if (todayNudges.length >= s.maxDailyNudges) {
      return { allowed: false, reason: `Maximum daily nudges reached (${s.maxDailyNudges}/day)` };
    }

    // Check duplicate prevention: same nudge type cannot be sent within 4 hours
    const fourHoursAgo = Date.now() - 4 * 60 * 60 * 1000;
    const duplicate = todayNudges.find(h => h.type === type && h.timestamp > fourHoursAgo);
    if (duplicate) {
      return { allowed: false, reason: 'Duplicate notification suppressed (sent recently)' };
    }

    return { allowed: true };
  }

  static recordNudgeSent(type, title, message) {
    const todayStr = new Date().toISOString().split('T')[0];
    const history = this.getSentHistory();
    const newEntry = {
      id: `nudge_${Date.now()}`,
      type,
      title,
      message,
      date: todayStr,
      timestamp: Date.now()
    };
    const updated = [newEntry, ...history].slice(0, 20); // keep last 20
    try {
      localStorage.setItem('saath_nudge_history', JSON.stringify(updated));
    } catch (e) {}
    return newEntry;
  }

  static generateContextualNudge({ userName = 'Friend', checkIn = null, pendingTasks = [], highStress = false }) {
    if (!checkIn) {
      return {
        type: NUDGE_TYPES.MORNING_CHECKIN,
        title: 'Morning Grounding',
        message: `Good morning, ${userName}! How are you feeling today? Tap to do your quick 30-second check-in.`
      };
    }

    if (highStress || (checkIn.stressLevel && checkIn.stressLevel >= 7)) {
      return {
        type: NUDGE_TYPES.FOCUS_REMINDER,
        title: 'Stress Reset Available',
        message: `Hey ${userName}, noticing elevated stress today. Take 2 minutes for a soothing 4-7-8 breathing pause.`
      };
    }

    if (pendingTasks.length > 0) {
      const firstTask = pendingTasks[0];
      return {
        type: NUDGE_TYPES.TASK_REMINDER,
        title: 'Gentle Focus Reminder',
        message: `Ready for a 25-minute focus session on "${firstTask.title || 'your top task'}"?`
      };
    }

    return {
      type: NUDGE_TYPES.EVENING_REFLECTION,
      title: 'Evening Reflection',
      message: `You made good progress today, ${userName}. Take a moment to wind down and celebrate showing up.`
    };
  }
}
