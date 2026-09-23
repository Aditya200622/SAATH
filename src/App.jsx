import React, { useState, useEffect, useRef } from 'react';
import {
  Home,
  MessageCircle,
  CheckSquare,
  Users,
  BarChart2,
  Heart,
  Shield,
  Settings,
  Play,
  Pause,
  RotateCcw,
  Plus,
  ArrowLeft,
  X,
  Phone,
  Send,
  Sparkles,
  Activity,
  Check,
  BatteryMedium,
  Wifi,
  Lock,
  User,
  Mail,
  Calendar,
  Compass,
  Smile,
  BookOpen,
  HelpCircle,
  RefreshCw,
  Bell,
  Clock,
  Info,
  CheckCircle2,
  AlertTriangle
} from 'lucide-react';
import SaathiAvatar from './components/SaathiAvatar';
import DemoModeBanner from './components/DemoModeBanner';
import {
  generateSaathiReply,
  runRealityEngine,
  checkSafety,
  EMERGENCY_RESOURCES
} from './services/aiService';
import { NudgeManager, NUDGE_TYPES } from './services/nudgeManager';

// Initial default tasks
const INITIAL_TASKS = [
  { id: 1, title: 'College Lecture • Engg Mechanics', time: '09:30 AM – 11:30 AM', category: 'College', completed: true, notes: 'Room 402' },
  { id: 2, title: 'Quiet Walk & Hydration', time: '11:30 AM – 12:30 PM', category: 'Wellness', completed: true, notes: '10 min fresh air' },
  { id: 3, title: 'Lunch & Mental Rest', time: '01:00 PM – 02:00 PM', category: 'Rest', completed: true, notes: 'Eat mindfully' },
  { id: 4, title: 'Study Block • Data Structures', time: '03:00 PM – 04:30 PM', category: 'Study', completed: false, notes: 'Trees & graphs' },
  { id: 5, title: 'Assignment Prep • C Programming', time: '05:00 PM – 06:30 PM', category: 'Assignment', completed: false, notes: 'Pointers & memory' },
  { id: 6, title: 'Evening Workout & Stretch', time: '07:30 PM – 08:30 PM', category: 'Workout', completed: false, notes: 'Core & cooldown' }
];

export default function App() {
  // Top-Level Screen Flow: 'login' | 'signup' | 'onboarding' | 'check_in' | 'daily_plan' | 'main'
  const [screen, setScreen] = useState('login');

  // Bottom Navigation Tab (when screen === 'main')
  const [currentTab, setCurrentTab] = useState('home'); // 'home' | 'chat' | 'tasks' | 'circle' | 'insights'

  // Modals & Overlays
  const [activeModal, setActiveModal] = useState(null); // 'reset' | 'focus' | 'journal' | 'safety' | 'settings' | 'support_request' | 'wearable_info' | 'nudge_settings'
  const [activeContactForSupport, setActiveContactForSupport] = useState(null);

  // User & Companion Profile (Persistent)
  const [userName, setUserName] = useState(() => localStorage.getItem('saath_user_name') || 'Aditya Mishra');
  const [userEmail, setUserEmail] = useState(() => localStorage.getItem('saath_user_email') || 'aditya@example.com');
  const [userAge, setUserAge] = useState(() => localStorage.getItem('saath_user_age') || '21');
  const [saathiName, setSaathiName] = useState(() => localStorage.getItem('saath_companion_name') || 'Aarav');
  const [saathiAvatar, setSaathiAvatar] = useState(() => localStorage.getItem('saath_avatar_id') || 'aarav');
  const [saathiVoice, setSaathiVoice] = useState('Calm Male');
  const [saathiPersonality, setSaathiPersonality] = useState('Empathetic & Grounding');
  const [saathiLanguage, setSaathiLanguage] = useState('Hinglish');

  // Avatar Expression Mood State
  const [avatarMood, setAvatarMood] = useState('idle'); // 'idle' | 'listening' | 'thinking' | 'speaking' | 'encouraging' | 'concerned'

  // Wearable Device State (EXPLICITLY LABELED AS DEMO / MOCK)
  const [wearableConnected, setWearableConnected] = useState(true);
  const [heartRate, setHeartRate] = useState(74); // Demo value from MockWearableAdapter
  const [stressScore, setStressScore] = useState(38); // Demo value from MockWearableAdapter
  const [stepsCount, setStepsCount] = useState(3420); // Demo value from MockWearableAdapter
  const [sleepScore, setSleepScore] = useState(85); // Demo sleep score
  const [sleepDuration, setSleepDuration] = useState('6h 20m'); // Demo sleep duration

  // Check-In Form State (Persistent)
  const [checkInMood, setCheckInMood] = useState(() => {
    try {
      const saved = localStorage.getItem('saath_checkin');
      if (saved) return JSON.parse(saved).mood || 'Good';
    } catch (e) {}
    return 'Good';
  });
  const [checkInStress, setCheckInStress] = useState(() => {
    try {
      const saved = localStorage.getItem('saath_checkin');
      if (saved) return JSON.parse(saved).stressLevel || 4;
    } catch (e) {}
    return 4;
  });
  const [checkInPriorities, setCheckInPriorities] = useState(() => {
    try {
      const saved = localStorage.getItem('saath_checkin');
      if (saved) return JSON.parse(saved).priorities || ['College Lecture', 'Study Block', 'Assignment'];
    } catch (e) {}
    return ['College Lecture', 'Study Block', 'Assignment'];
  });
  const [checkInNote, setCheckInNote] = useState(() => {
    try {
      const saved = localStorage.getItem('saath_checkin');
      if (saved) return JSON.parse(saved).note || '';
    } catch (e) {}
    return '';
  });

  // Tasks State with Persistent Local Storage (Room parallel)
  const [tasks, setTasks] = useState(() => {
    try {
      const saved = localStorage.getItem('saath_tasks_data');
      if (saved) {
        const parsed = JSON.parse(saved);
        if (Array.isArray(parsed) && parsed.length > 0) return parsed;
      }
    } catch (e) {}
    return INITIAL_TASKS;
  });

  // Sync tasks to local storage whenever they change
  useEffect(() => {
    try {
      localStorage.setItem('saath_tasks_data', JSON.stringify(tasks));
    } catch (e) {}
  }, [tasks]);

  // Sync profile & companion to local storage
  useEffect(() => {
    try {
      localStorage.setItem('saath_user_name', userName);
      localStorage.setItem('saath_user_email', userEmail);
      localStorage.setItem('saath_user_age', userAge);
      localStorage.setItem('saath_companion_name', saathiName);
      localStorage.setItem('saath_avatar_id', saathiAvatar);
    } catch (e) {}
  }, [userName, userEmail, userAge, saathiName, saathiAvatar]);

  // AI Chat Messages
  const [messages, setMessages] = useState([
    {
      id: 1,
      sender: 'saathi',
      text: `Namaste ${userName.split(' ')[0]}! 🙏 I'm right here with you. How are you feeling today?`,
      chips: ["I'm feeling good 🙂", "A bit stressed about tomorrow", "Need help organizing my day"]
    }
  ]);
  const [chatInput, setChatInput] = useState('');
  const [isAiThinking, setIsAiThinking] = useState(false);
  const [activeTaskBreakdown, setActiveTaskBreakdown] = useState(null);
  const [showToast, setShowToast] = useState(null);

  // 2-Minute Reset State
  const [isResetRunning, setIsResetRunning] = useState(false);
  const [resetPhase, setResetPhase] = useState('Inhale'); // 'Inhale' (4s) | 'Hold' (7s) | 'Exhale' (8s)
  const [resetSeconds, setResetSeconds] = useState(4);
  const [resetCycle, setResetCycle] = useState(1);

  // 25-Minute Focus State
  const [focusTask, setFocusTask] = useState('Assignment Prep • C Programming');
  const [focusSecondsLeft, setFocusSecondsLeft] = useState(25 * 60);
  const [isFocusRunning, setIsFocusRunning] = useState(false);

  // Journal State
  const [journalEntries, setJournalEntries] = useState(() => {
    try {
      const saved = localStorage.getItem('saath_journal');
      if (saved) return JSON.parse(saved);
    } catch (e) {}
    return [
      { id: 1, date: 'Yesterday • 09:30 PM', text: 'Managed to complete the database schema. Breathing exercise helped calm down before bed.' }
    ];
  });
  const [journalInput, setJournalInput] = useState('');

  // Trusted Contacts
  const [contacts, setContacts] = useState([
    { id: 1, name: 'Mom', relation: 'Family', phone: '+91 98111 22334', initial: 'M', color: '#1C3F30' },
    { id: 2, name: 'Dad', relation: 'Family', phone: '+91 98222 33445', initial: 'D', color: '#2C5E48' },
    { id: 3, name: 'Karan Sharma', relation: 'Roommate & Friend', phone: '+91 98333 44556', initial: 'K', color: '#1565C0' },
    { id: 4, name: 'Dr. Priya Verma', relation: 'Campus Counselor', phone: '+91 98444 55667', initial: 'P', color: '#6A1B9A' }
  ]);

  // Nudge System State
  const [activeNudge, setActiveNudge] = useState(null);
  const [nudgeSettings, setNudgeSettings] = useState(() => NudgeManager.getSettings());

  // Check for smart nudge on screen enter or task update
  useEffect(() => {
    if (screen === 'main' && currentTab === 'home') {
      const pending = tasks.filter((t) => !t.completed);
      const isHighStress = checkInStress >= 7;
      const suggestion = NudgeManager.generateContextualNudge({
        userName: userName.split(' ')[0],
        checkIn: { mood: checkInMood, stressLevel: checkInStress },
        pendingTasks: pending,
        highStress: isHighStress
      });

      const check = NudgeManager.canTriggerNudge(suggestion.type, nudgeSettings);
      if (check.allowed) {
        setActiveNudge(suggestion);
        NudgeManager.recordNudgeSent(suggestion.type, suggestion.title, suggestion.message);
      }
    }
  }, [screen, currentTab, checkInStress, checkInMood, nudgeSettings]);

  // 90-Second Deterministic Demo Controller
  const [isDemoRunning, setIsDemoRunning] = useState(false);
  const [demoStep, setDemoStep] = useState(0);
  const [demoSecondsLeft, setDemoSecondsLeft] = useState(90);
  const demoIntervalRef = useRef(null);

  // Breathing Loop (4-7-8 method)
  useEffect(() => {
    let interval = null;
    if (isResetRunning) {
      interval = setInterval(() => {
        setResetSeconds((prev) => {
          if (prev <= 1) {
            if (resetPhase === 'Inhale') {
              setResetPhase('Hold');
              return 7;
            } else if (resetPhase === 'Hold') {
              setResetPhase('Exhale');
              return 8;
            } else {
              setResetPhase('Inhale');
              setResetCycle((c) => (c >= 4 ? 1 : c + 1));
              return 4;
            }
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [isResetRunning, resetPhase]);

  // Focus Countdown Loop
  useEffect(() => {
    let timer = null;
    if (isFocusRunning && focusSecondsLeft > 0) {
      timer = setInterval(() => {
        setFocusSecondsLeft((sec) => sec - 1);
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [isFocusRunning, focusSecondsLeft]);

  // Chat message sender connected to Real AI & Reality Engine
  const handleSendMessage = async (textToSend) => {
    const text = textToSend || chatInput;
    if (!text.trim()) return;

    const newMsg = { id: Date.now(), sender: 'user', text };
    setMessages((prev) => [...prev, newMsg]);
    setChatInput('');
    setIsAiThinking(true);
    setAvatarMood('thinking');

    try {
      const response = await generateSaathiReply({
        userMessage: text,
        userName: userName.split(' ')[0],
        saathiName,
        checkIn: { mood: checkInMood, stressLevel: checkInStress, note: checkInNote },
        tasks
      });

      setIsAiThinking(false);
      setAvatarMood(response.avatarMood || 'speaking');

      if (response.safetyLevel === 'HIGH_RISK') {
        setActiveModal('safety');
      }

      if (response.breakdown) {
        setActiveTaskBreakdown(response.breakdown);
      }

      setMessages((prev) => [
        ...prev,
        {
          id: Date.now() + 1,
          sender: 'saathi',
          text: response.text,
          chips: response.suggestionChips || [],
          action: response.actionType,
          breakdown: response.breakdown
        }
      ]);

      // Return avatar to idle after 4 seconds
      setTimeout(() => {
        setAvatarMood('idle');
      }, 4000);
    } catch (e) {
      setIsAiThinking(false);
      setAvatarMood('concerned');
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now() + 1,
          sender: 'saathi',
          text: "I'm right here with you. Let's take a calm pause together. 💚",
          chips: ['2 Min Reset', '25 Min Focus']
        }
      ]);
    }
  };

  // Add micro tasks created by Reality Engine directly to the persistent Task list
  const handleCreateTasksFromBreakdown = (breakdown) => {
    if (!breakdown || !breakdown.steps) return;
    const newItems = breakdown.steps.map((step, idx) => ({
      id: Date.now() + idx,
      title: step,
      time: `Sprint Step ${idx + 1}`,
      category: 'Micro-Step',
      completed: false,
      notes: `Decomposed from ${breakdown.stressor}`
    }));

    setTasks((prev) => [...prev, ...newItems]);
    setShowToast(`Added ${newItems.length} micro-steps to Today's Tasks! ✅`);
    setActiveTaskBreakdown(null);
    setTimeout(() => setShowToast(null), 3000);
  };

  // Submit Daily Check-in Form
  const handleSubmitCheckIn = () => {
    const checkInData = {
      mood: checkInMood,
      stressLevel: checkInStress,
      priorities: checkInPriorities,
      note: checkInNote,
      timestamp: Date.now()
    };
    try {
      localStorage.setItem('saath_checkin', JSON.stringify(checkInData));
    } catch (e) {}

    // Adapt stress metric on home screen
    setStressScore(checkInStress * 9); // reflect user reported stress on demo metric

    // Automatically adapt Daily Plan based on submitted check-in
    const adaptiveSchedule = [];
    let startHour = 9;

    checkInPriorities.forEach((p, idx) => {
      adaptiveSchedule.push({
        id: Date.now() + idx,
        title: `${p} Session`,
        time: `${String(startHour).padStart(2, '0')}:00 – ${String(startHour + 1).padStart(2, '0')}:30`,
        category: p,
        completed: false,
        notes: `Prioritized in check-in • ${checkInNote ? `"${checkInNote.slice(0, 24)}..."` : 'Focused block'}`
      });
      startHour += 2;

      // If user indicated high stress (>= 7), insert calming pauses
      if (checkInStress >= 7 && idx === 0) {
        adaptiveSchedule.push({
          id: Date.now() + 100,
          title: '🌿 Calming 4-7-8 Breathing Reset',
          time: `${String(startHour - 1).padStart(2, '0')}:30 – ${String(startHour).padStart(2, '0')}:00`,
          category: 'Wellness',
          completed: false,
          notes: 'Automated Reality Engine nervous system pause'
        });
      }
    });

    if (adaptiveSchedule.length > 0) {
      setTasks(adaptiveSchedule);
    }

    setScreen('daily_plan');
  };

  const toggleTask = (id) => {
    setTasks((prev) => prev.map((t) => (t.id === id ? { ...t, completed: !t.completed } : t)));
  };

  // Save new Journal Entry
  const handleSaveJournal = () => {
    if (!journalInput.trim()) return;
    const entry = {
      id: Date.now(),
      date: 'Today • Just now',
      text: journalInput.trim()
    };
    const updated = [entry, ...journalEntries];
    setJournalEntries(updated);
    try {
      localStorage.setItem('saath_journal', JSON.stringify(updated));
    } catch (e) {}
    setJournalInput('');
    setShowToast('Journal entry saved privately 🔒');
    setTimeout(() => setShowToast(null), 3000);
  };

  // 90-Second Deterministic Demo Runner
  const startDeterministicDemo = () => {
    setIsDemoRunning(true);
    setDemoStep(1);
    setDemoSecondsLeft(90);

    // Step 1 (0s): Check-in with High Stress
    setScreen('check_in');
    setCheckInMood('Hard');
    setCheckInStress(8);
    setCheckInPriorities(['Assignment', 'Study', 'College Lecture']);
    setCheckInNote('Assignment pending tomorrow morning, feeling behind');
  };

  const stopDeterministicDemo = () => {
    setIsDemoRunning(false);
    setDemoStep(0);
    if (demoIntervalRef.current) clearInterval(demoIntervalRef.current);
  };

  useEffect(() => {
    if (!isDemoRunning) return;

    demoIntervalRef.current = setInterval(() => {
      setDemoSecondsLeft((prev) => {
        if (prev <= 1) {
          stopDeterministicDemo();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(demoIntervalRef.current);
  }, [isDemoRunning]);

  // Timed step progression for the 90s demo
  useEffect(() => {
    if (!isDemoRunning) return;

    const remaining = demoSecondsLeft;

    // Step 1: 90s - 80s: Check-in Screen (High Stress = 8)
    if (remaining === 80 && demoStep === 1) {
      setDemoStep(2);
      handleSubmitCheckIn(); // transitions to 'daily_plan'
    }

    // Step 2: 80s - 65s: Daily Plan Screen
    if (remaining === 65 && demoStep === 2) {
      setDemoStep(3);
      setScreen('main');
      setCurrentTab('chat');
      setTimeout(() => {
        handleSendMessage('I am stressed because my assignment is pending.');
      }, 500);
    }

    // Step 3: 65s - 45s: Saathi response & 3 micro tasks
    if (remaining === 45 && demoStep === 3) {
      setDemoStep(4);
      // Auto-create the tasks from breakdown
      const breakdown = runRealityEngine('I am stressed because my assignment is pending.', userName.split(' ')[0]);
      handleCreateTasksFromBreakdown(breakdown);
    }

    // Step 4: 45s - 30s: 2-Minute Breathing Reset
    if (remaining === 30 && demoStep === 4) {
      setDemoStep(5);
      setActiveModal('reset');
      setIsResetRunning(true);
      setResetPhase('Inhale');
      setResetSeconds(4);
    }

    // Step 5: 30s - 15s: 25-Minute Focus Session
    if (remaining === 15 && demoStep === 5) {
      setDemoStep(6);
      setActiveModal('focus');
      setFocusTask('Assignment Micro-Step 1: Open file and outline 3 points');
      setIsFocusRunning(true);
    }

    // Step 6: 15s - 5s: Support Option
    if (remaining === 5 && demoStep === 6) {
      setDemoStep(7);
      setActiveModal('support_request');
      setActiveContactForSupport(contacts[0]); // Mom
    }

    // Step 7: 0s: Home screen updated
    if (remaining === 1 && demoStep === 7) {
      setDemoStep(8);
      setActiveModal(null);
      setCurrentTab('home');
      setAvatarMood('encouraging');
      setShowToast('Deterministic 90s Demo Completed Successfully! 🎉');
      setTimeout(() => setShowToast(null), 4000);
      stopDeterministicDemo();
    }
  }, [demoSecondsLeft, isDemoRunning, demoStep]);

  return (
    <div className="phone-viewport-container">
      <div className="android-frame">
        {/* Android Native Status Bar */}
        <div className="status-bar">
          <span>09:41</span>
          <div className="icons">
            <Wifi size={14} />
            <BatteryMedium size={14} />
          </div>
        </div>

        {/* Floating Deterministic Demo Banner if active */}
        {isDemoRunning && (
          <div
            style={{
              backgroundColor: '#E65100',
              color: '#FFF',
              padding: '6px 12px',
              fontSize: '11px',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              zIndex: 100
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Sparkles size={14} />
              <span style={{ fontWeight: '800' }}>
                90s DEMO • STEP {demoStep}/7 ({demoSecondsLeft}s left)
              </span>
            </div>
            <button
              onClick={stopDeterministicDemo}
              style={{
                background: '#BF360C',
                border: 'none',
                color: '#FFF',
                borderRadius: '4px',
                padding: '2px 8px',
                fontSize: '10px',
                cursor: 'pointer',
                fontWeight: '700'
              }}
            >
              Stop Demo
            </button>
          </div>
        )}

        {/* Global Nav Ribbon / Flow Switcher for Testing */}
        <div
          style={{
            backgroundColor: '#0E3524',
            color: '#FAF9F6',
            padding: '6px 12px',
            fontSize: '11px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            borderBottom: '1px solid #1C3F30',
            zIndex: 50
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <span style={{ fontWeight: '800', color: '#D6E8DE' }}>SAATH</span>
            <span style={{ color: '#A3C2B0' }}>• {screen.toUpperCase()}</span>
          </div>

          <div style={{ display: 'flex', gap: '6px' }}>
            {!isDemoRunning && (
              <button
                onClick={startDeterministicDemo}
                style={{
                  background: '#E65100',
                  color: '#FAF9F6',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '2px 8px',
                  fontSize: '10px',
                  fontWeight: '800',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px'
                }}
                title="Runs the required deterministic 90s demo flow"
              >
                <Play size={10} fill="#FAF9F6" /> 90s Demo
              </button>
            )}
            {screen !== 'login' && (
              <button
                onClick={() => setScreen('login')}
                style={{
                  background: '#1C3F30',
                  color: '#FAF9F6',
                  border: '1px solid #285A45',
                  borderRadius: '6px',
                  padding: '2px 8px',
                  fontSize: '10px',
                  cursor: 'pointer'
                }}
              >
                Flow ↺
              </button>
            )}
            {screen !== 'main' && (
              <button
                onClick={() => {
                  setScreen('main');
                  setCurrentTab('home');
                }}
                style={{
                  background: '#2E7D32',
                  color: '#FAF9F6',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '2px 8px',
                  fontSize: '10px',
                  fontWeight: '700',
                  cursor: 'pointer'
                }}
              >
                Home ➔
              </button>
            )}
          </div>
        </div>

        {/* Global Toast Notification */}
        {showToast && (
          <div
            style={{
              position: 'absolute',
              top: '80px',
              left: '16px',
              right: '16px',
              backgroundColor: '#1C3F30',
              color: '#FAF9F6',
              padding: '10px 14px',
              borderRadius: '12px',
              fontSize: '12px',
              fontWeight: '700',
              zIndex: 90,
              boxShadow: '0 6px 16px rgba(0,0,0,0.3)',
              display: 'flex',
              alignItems: 'center',
              gap: '8px'
            }}
          >
            <CheckCircle2 size={16} color="#4CAF50" />
            <span>{showToast}</span>
          </div>
        )}

        {/* Screen Scroll Area */}
        <div className="screen-scroll-container">
          {/* ========================================================================= */}
          {/* SCREEN 1: LOGIN */}
          {screen === 'login' && (
            <div
              style={{
                flex: 1,
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                padding: '28px 24px',
                backgroundColor: '#FAF9F6'
              }}
            >
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center', marginTop: '20px' }}>
                <div
                  style={{
                    width: '74px',
                    height: '74px',
                    borderRadius: '50%',
                    backgroundColor: '#1C3F30',
                    color: '#FAF9F6',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: '800',
                    fontSize: '32px',
                    boxShadow: '0 8px 24px rgba(28, 63, 48, 0.2)'
                  }}
                >
                  S
                </div>
                <h1 style={{ fontSize: '26px', fontWeight: '800', color: '#1C3F30', marginTop: '14px', marginBottom: '4px' }}>SAATH</h1>
                <p style={{ fontSize: '13px', color: '#58696D', fontWeight: '600' }}>Always with you • 24x7 AI Companion</p>
                <div style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', backgroundColor: '#E8F2EC', padding: '4px 10px', borderRadius: '12px', marginTop: '8px' }}>
                  <Shield size={12} color="#1C3F30" />
                  <span style={{ fontSize: '10px', fontWeight: '700', color: '#1C3F30' }}>Emotional Support & Task Pacing</span>
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', margin: '20px 0' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  <label style={{ fontSize: '12px', fontWeight: '700', color: '#15222E' }}>Email Address</label>
                  <input
                    type="email"
                    value={userEmail}
                    onChange={(e) => setUserEmail(e.target.value)}
                    placeholder="Enter your email"
                    style={{
                      padding: '12px 14px',
                      borderRadius: '12px',
                      border: '1px solid #E2ECE5',
                      backgroundColor: '#FFFFFF',
                      fontSize: '13px',
                      outline: 'none'
                    }}
                  />
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  <label style={{ fontSize: '12px', fontWeight: '700', color: '#15222E' }}>Password</label>
                  <input
                    type="password"
                    defaultValue="••••••••"
                    placeholder="Enter password"
                    style={{
                      padding: '12px 14px',
                      borderRadius: '12px',
                      border: '1px solid #E2ECE5',
                      backgroundColor: '#FFFFFF',
                      fontSize: '13px',
                      outline: 'none'
                    }}
                  />
                </div>

                <button
                  onClick={() => setScreen('check_in')}
                  style={{
                    backgroundColor: '#1C3F30',
                    color: '#FFFFFF',
                    border: 'none',
                    borderRadius: '14px',
                    padding: '14px',
                    fontSize: '14px',
                    fontWeight: '800',
                    cursor: 'pointer',
                    marginTop: '8px'
                  }}
                >
                  Log In to SAATH ➔
                </button>

                <button
                  onClick={() => setScreen('signup')}
                  style={{
                    backgroundColor: 'transparent',
                    color: '#1C3F30',
                    border: '1px solid #1C3F30',
                    borderRadius: '14px',
                    padding: '12px',
                    fontSize: '13px',
                    fontWeight: '700',
                    cursor: 'pointer'
                  }}
                >
                  Don't have an account? Sign Up
                </button>
              </div>

              <div style={{ textAlign: 'center', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <button
                  onClick={() => {
                    setScreen('main');
                    setCurrentTab('home');
                  }}
                  style={{
                    background: 'none',
                    border: 'none',
                    color: '#285A45',
                    fontSize: '12px',
                    fontWeight: '700',
                    textDecoration: 'underline',
                    cursor: 'pointer'
                  }}
                >
                  ⚡ Skip to Demo Mode (Home Screen)
                </button>

                <button
                  onClick={startDeterministicDemo}
                  style={{
                    background: '#FFF3E0',
                    border: '1px solid #FFE0B2',
                    borderRadius: '8px',
                    color: '#E65100',
                    fontSize: '11px',
                    fontWeight: '800',
                    padding: '6px',
                    cursor: 'pointer'
                  }}
                >
                  ▶ Launch 90s Deterministic Hardened Demo
                </button>
              </div>
            </div>
          )}

          {/* ========================================================================= */}
          {/* SCREEN 2: SIGN UP */}
          {screen === 'signup' && (
            <div
              style={{
                flex: 1,
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                padding: '24px',
                backgroundColor: '#FAF9F6'
              }}
            >
              <div>
                <button
                  onClick={() => setScreen('login')}
                  style={{ background: 'none', border: 'none', display: 'flex', alignItems: 'center', gap: '4px', color: '#1C3F30', cursor: 'pointer', marginBottom: '14px' }}
                >
                  <ArrowLeft size={16} /> Back to Login
                </button>

                <h2 style={{ fontSize: '22px', fontWeight: '800', color: '#15222E' }}>Create Your Account 🌿</h2>
                <p style={{ fontSize: '12px', color: '#58696D', marginTop: '4px' }}>
                  Begin your companion journey for emotional balance and focus.
                </p>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginTop: '20px' }}>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '11px', fontWeight: '700', color: '#15222E' }}>Full Name</label>
                    <input
                      type="text"
                      value={userName}
                      onChange={(e) => setUserName(e.target.value)}
                      style={{ padding: '10px 12px', borderRadius: '10px', border: '1px solid #E2ECE5', backgroundColor: '#FFF', fontSize: '13px' }}
                    />
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '11px', fontWeight: '700', color: '#15222E' }}>Email Address</label>
                    <input
                      type="email"
                      value={userEmail}
                      onChange={(e) => setUserEmail(e.target.value)}
                      style={{ padding: '10px 12px', borderRadius: '10px', border: '1px solid #E2ECE5', backgroundColor: '#FFF', fontSize: '13px' }}
                    />
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '11px', fontWeight: '700', color: '#15222E' }}>Age</label>
                    <input
                      type="number"
                      value={userAge}
                      onChange={(e) => setUserAge(e.target.value)}
                      style={{ padding: '10px 12px', borderRadius: '10px', border: '1px solid #E2ECE5', backgroundColor: '#FFF', fontSize: '13px' }}
                    />
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '11px', fontWeight: '700', color: '#15222E' }}>Password</label>
                    <input
                      type="password"
                      defaultValue="••••••••"
                      style={{ padding: '10px 12px', borderRadius: '10px', border: '1px solid #E2ECE5', backgroundColor: '#FFF', fontSize: '13px' }}
                    />
                  </div>
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginTop: '20px' }}>
                <button
                  onClick={() => setScreen('onboarding')}
                  style={{
                    backgroundColor: '#1C3F30',
                    color: '#FFF',
                    border: 'none',
                    borderRadius: '14px',
                    padding: '14px',
                    fontSize: '14px',
                    fontWeight: '800',
                    cursor: 'pointer'
                  }}
                >
                  Continue to Companion Setup ➔
                </button>
              </div>
            </div>
          )}

          {/* ========================================================================= */}
          {/* SCREEN 3: ONBOARDING (COMPANION SETUP) */}
          {screen === 'onboarding' && (
            <div
              style={{
                flex: 1,
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                padding: '24px',
                backgroundColor: '#FAF9F6'
              }}
            >
              <div>
                <h2 style={{ fontSize: '20px', fontWeight: '800', color: '#15222E' }}>Meet Your Saathi 🤝</h2>
                <p style={{ fontSize: '12px', color: '#58696D', marginTop: '4px' }}>
                  Personalize your companion's voice, personality, and avatar.
                </p>

                {/* Avatar Preview using Unified SaathiAvatar */}
                <div style={{ display: 'flex', justifyContent: 'center', margin: '20px 0' }}>
                  <SaathiAvatar name={saathiName} avatarId={saathiAvatar} size={84} mood={avatarMood} showGlow={true} />
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '11px', fontWeight: '700', color: '#15222E' }}>Companion Name</label>
                    <input
                      type="text"
                      value={saathiName}
                      onChange={(e) => setSaathiName(e.target.value)}
                      style={{ padding: '10px 12px', borderRadius: '10px', border: '1px solid #E2ECE5', backgroundColor: '#FFF', fontSize: '13px' }}
                    />
                  </div>

                  {/* Avatar Picker */}
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '11px', fontWeight: '700', color: '#15222E' }}>Choose Avatar Style</label>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '8px' }}>
                      {[
                        { id: 'aarav', label: 'Aarav (Green)' },
                        { id: 'mira', label: 'Mira (Rose)' },
                        { id: 'kian', label: 'Kian (Blue)' },
                        { id: 'nova', label: 'Nova (Warm)' }
                      ].map((av) => (
                        <button
                          key={av.id}
                          onClick={() => setSaathiAvatar(av.id)}
                          style={{
                            padding: '8px 4px',
                            borderRadius: '10px',
                            border: saathiAvatar === av.id ? '2px solid #1C3F30' : '1px solid #E2ECE5',
                            backgroundColor: saathiAvatar === av.id ? '#D6E8DE' : '#FFF',
                            fontSize: '11px',
                            fontWeight: '700',
                            cursor: 'pointer'
                          }}
                        >
                          {av.label.split(' ')[0]}
                        </button>
                      ))}
                    </div>
                  </div>

                  {/* Personality Style */}
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '11px', fontWeight: '700', color: '#15222E' }}>Companion Persona</label>
                    <select
                      value={saathiPersonality}
                      onChange={(e) => setSaathiPersonality(e.target.value)}
                      style={{ padding: '10px 12px', borderRadius: '10px', border: '1px solid #E2ECE5', backgroundColor: '#FFF', fontSize: '13px' }}
                    >
                      <option>Empathetic & Grounding</option>
                      <option>Action-Oriented & Direct</option>
                      <option>Gentle & Reflective</option>
                    </select>
                  </div>
                </div>
              </div>

              <button
                onClick={() => setScreen('check_in')}
                style={{
                  backgroundColor: '#1C3F30',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '14px',
                  padding: '14px',
                  fontSize: '14px',
                  fontWeight: '800',
                  cursor: 'pointer',
                  marginTop: '16px'
                }}
              >
                Start Morning Check-in 🌅
              </button>
            </div>
          )}

          {/* ========================================================================= */}
          {/* SCREEN 4: MORNING CHECK-IN */}
          {screen === 'check_in' && (
            <div
              style={{
                flex: 1,
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                padding: '24px',
                backgroundColor: '#FAF9F6'
              }}
            >
              <div>
                <span style={{ fontSize: '11px', fontWeight: '800', color: '#1C3F30' }}>STEP 1 OF 2 • MORNING ALIGNMENT</span>
                <h2 style={{ fontSize: '20px', fontWeight: '800', color: '#15222E', marginTop: '4px' }}>
                  Good morning, {userName.split(' ')[0]}! 🌅
                </h2>
                <p style={{ fontSize: '12px', color: '#58696D', marginTop: '2px' }}>
                  How are you feeling as you start your day?
                </p>

                {/* Mood Selection */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '8px', margin: '16px 0' }}>
                  {[
                    { label: 'Great', emoji: '😄' },
                    { label: 'Good', emoji: '🙂' },
                    { label: 'Okay', emoji: '😐' },
                    { label: 'Hard', emoji: '😫' }
                  ].map((m) => (
                    <button
                      key={m.label}
                      onClick={() => setCheckInMood(m.label)}
                      style={{
                        backgroundColor: checkInMood === m.label ? '#D6E8DE' : '#FFF',
                        border: checkInMood === m.label ? '2px solid #1C3F30' : '1px solid #E2ECE5',
                        borderRadius: '14px',
                        padding: '10px 4px',
                        cursor: 'pointer',
                        textAlign: 'center'
                      }}
                    >
                      <div style={{ fontSize: '22px' }}>{m.emoji}</div>
                      <div style={{ fontSize: '11px', fontWeight: '700', marginTop: '4px', color: '#15222E' }}>{m.label}</div>
                    </button>
                  ))}
                </div>

                {/* Stress Slider */}
                <div style={{ backgroundColor: '#FFF', border: '1px solid #E2ECE5', borderRadius: '16px', padding: '14px', marginBottom: '14px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', fontWeight: '700' }}>
                    <span>Self-Assessed Stress Level:</span>
                    <span style={{ color: checkInStress >= 7 ? '#C62828' : '#1C3F30', fontWeight: '800' }}>
                      {checkInStress} / 10 {checkInStress >= 7 ? '⚠️ (High)' : ''}
                    </span>
                  </div>
                  <input
                    type="range"
                    min="1"
                    max="10"
                    value={checkInStress}
                    onChange={(e) => setCheckInStress(Number(e.target.value))}
                    style={{ width: '100%', accentColor: '#1C3F30', marginTop: '10px' }}
                  />
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '10px', color: '#58696D', marginTop: '4px' }}>
                    <span>1 (Calm)</span>
                    <span>5 (Moderate)</span>
                    <span>10 (Overwhelmed)</span>
                  </div>
                </div>

                {/* Priorities Chips */}
                <div style={{ marginBottom: '14px' }}>
                  <div style={{ fontSize: '12px', fontWeight: '700', marginBottom: '8px' }}>Today's Priorities:</div>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                    {['College Lecture', 'Study Block', 'Assignment', 'Workout', 'Social Time', 'Rest'].map((p) => {
                      const sel = checkInPriorities.includes(p);
                      return (
                        <button
                          key={p}
                          onClick={() => {
                            setCheckInPriorities(sel ? checkInPriorities.filter((x) => x !== p) : [...checkInPriorities, p]);
                          }}
                          style={{
                            backgroundColor: sel ? '#1C3F30' : '#FFF',
                            color: sel ? '#FFF' : '#15222E',
                            border: sel ? '1px solid #1C3F30' : '1px solid #E2ECE5',
                            borderRadius: '12px',
                            padding: '6px 10px',
                            fontSize: '11px',
                            fontWeight: '600',
                            cursor: 'pointer'
                          }}
                        >
                          {p}
                        </button>
                      );
                    })}
                  </div>
                </div>

                {/* Optional Note (Verified Feature) */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                  <label style={{ fontSize: '11px', fontWeight: '700', color: '#15222E' }}>
                    What's on your mind? (Optional Note)
                  </label>
                  <input
                    type="text"
                    value={checkInNote}
                    onChange={(e) => setCheckInNote(e.target.value)}
                    placeholder="e.g. Assignment pending tomorrow morning"
                    style={{
                      padding: '10px 12px',
                      borderRadius: '10px',
                      border: '1px solid #E2ECE5',
                      backgroundColor: '#FFF',
                      fontSize: '12px',
                      outline: 'none'
                    }}
                  />
                </div>
              </div>

              <button
                onClick={handleSubmitCheckIn}
                style={{
                  backgroundColor: '#1C3F30',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '16px',
                  padding: '14px',
                  fontSize: '14px',
                  fontWeight: '800',
                  cursor: 'pointer',
                  marginTop: '16px'
                }}
              >
                Generate Reality Engine Daily Plan ✨
              </button>
            </div>
          )}

          {/* ========================================================================= */}
          {/* SCREEN 5: DAILY PLAN */}
          {screen === 'daily_plan' && (
            <div
              style={{
                flex: 1,
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                padding: '24px',
                backgroundColor: '#FAF9F6'
              }}
            >
              <div>
                <span style={{ fontSize: '11px', fontWeight: '800', color: '#1C3F30' }}>STEP 2 OF 2 • REALITY ENGINE PLAN</span>
                <h2 style={{ fontSize: '20px', fontWeight: '800', color: '#15222E', marginTop: '4px' }}>
                  Your Balanced Day 🎯
                </h2>

                <div
                  style={{
                    backgroundColor: checkInStress >= 7 ? '#FFF3E0' : '#D6E8DE',
                    border: `1px solid ${checkInStress >= 7 ? '#FFE0B2' : '#CBEAD7'}`,
                    borderRadius: '14px',
                    padding: '12px',
                    fontSize: '12px',
                    color: checkInStress >= 7 ? '#BF360C' : '#1C3F30',
                    margin: '14px 0',
                    lineHeight: '17px'
                  }}
                >
                  <strong>{saathiName}:</strong>{' '}
                  {checkInStress >= 7
                    ? `Noticing high stress (${checkInStress}/10). I've softened your schedule, prioritized single-task blocks, and inserted a calming 4-7-8 breathing reset.`
                    : `Based on your ${checkInMood.toLowerCase()} mood and priorities, I have balanced your schedule with productive sprints and restorative breaks.`}
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {tasks.map((t, idx) => (
                    <div key={t.id} style={{ backgroundColor: '#FFF', border: '1px solid #E2ECE5', borderRadius: '12px', padding: '10px 12px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <div style={{ fontSize: '13px', fontWeight: '700', color: '#15222E' }}>
                          {idx + 1}. {t.title}
                        </div>
                        <span style={{ fontSize: '10px', backgroundColor: '#E8F2EC', color: '#1C3F30', padding: '2px 6px', borderRadius: '6px', fontWeight: '700' }}>
                          {t.category}
                        </span>
                      </div>
                      <div style={{ fontSize: '11px', color: '#58696D', marginTop: '2px' }}>{t.time} • {t.notes}</div>
                    </div>
                  ))}
                </div>
              </div>

              <button
                onClick={() => {
                  setScreen('main');
                  setCurrentTab('home');
                }}
                style={{
                  backgroundColor: '#1C3F30',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '16px',
                  padding: '14px',
                  fontSize: '14px',
                  fontWeight: '800',
                  cursor: 'pointer',
                  marginTop: '16px'
                }}
              >
                Accept Plan & Enter SAATH 🚀
              </button>
            </div>
          )}

          {/* ========================================================================= */}
          {/* SCREEN 6: MAIN APP (HOME, SAATHI CHAT, TASKS, CIRCLE, INSIGHTS) */}
          {screen === 'main' && currentTab === 'home' && (
            <div style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {/* Header */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <SaathiAvatar name={saathiName} avatarId={saathiAvatar} size={44} mood={avatarMood} showGlow={true} />
                  <div>
                    <h2 style={{ fontSize: '18px', fontWeight: '800', color: '#1C3F30', margin: 0 }}>SAATH</h2>
                    <span style={{ fontSize: '11px', color: '#58696D', fontWeight: '600' }}>
                      Always with you • {saathiName}
                    </span>
                  </div>
                </div>

                <div style={{ display: 'flex', gap: '8px' }}>
                  <button
                    onClick={() => setActiveModal('safety')}
                    style={{
                      background: '#FFEBEE',
                      border: '1px solid #FFCDD2',
                      borderRadius: '50%',
                      width: '36px',
                      height: '36px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      color: '#C62828',
                      cursor: 'pointer'
                    }}
                    title="Safety & 24/7 Helplines"
                  >
                    <Shield size={18} />
                  </button>
                  <button
                    onClick={() => setActiveModal('settings')}
                    style={{
                      background: '#FFFFFF',
                      border: '1px solid #E2ECE5',
                      borderRadius: '50%',
                      width: '36px',
                      height: '36px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      color: '#1C3F30',
                      cursor: 'pointer'
                    }}
                    title="Settings"
                  >
                    <Settings size={18} />
                  </button>
                </div>
              </div>

              {/* Reusable Demo Mode Status Banner */}
              <DemoModeBanner
                onStartDemo={startDeterministicDemo}
                onOpenAuditModal={() => setActiveModal('wearable_info')}
              />

              {/* In-App Smart Nudge Banner (if active) */}
              {activeNudge && (
                <div
                  style={{
                    backgroundColor: '#E8F2EC',
                    border: '1px solid #CBEAD7',
                    borderRadius: '16px',
                    padding: '12px 14px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    gap: '8px'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Bell size={18} color="#1C3F30" />
                    <div>
                      <div style={{ fontSize: '12px', fontWeight: '800', color: '#1C3F30' }}>{activeNudge.title}</div>
                      <div style={{ fontSize: '11px', color: '#285A45' }}>{activeNudge.message}</div>
                    </div>
                  </div>
                  <button
                    onClick={() => setActiveNudge(null)}
                    style={{ background: 'none', border: 'none', color: '#58696D', cursor: 'pointer' }}
                  >
                    <X size={16} />
                  </button>
                </div>
              )}

              {/* Connected Wearable Metrics Card (PROPERLY LABELED AS DEMO DATA) */}
              <div
                style={{
                  background: '#FFFFFF',
                  border: '1px solid #E2ECE5',
                  borderRadius: '20px',
                  padding: '16px'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Activity size={16} color="#1C3F30" />
                    <span style={{ fontSize: '13px', fontWeight: '800', color: '#15222E' }}>Amazfit Band 7</span>
                    <span
                      style={{
                        fontSize: '9px',
                        fontWeight: '800',
                        backgroundColor: '#FFF3E0',
                        color: '#E65100',
                        padding: '2px 6px',
                        borderRadius: '6px',
                        border: '1px solid #FFE0B2'
                      }}
                      title="Simulated sensor stream from MockWearableAdapter"
                    >
                      DEMO WEARABLE DATA
                    </span>
                  </div>
                  <button
                    onClick={() => setWearableConnected(!wearableConnected)}
                    style={{
                      background: 'none',
                      border: 'none',
                      fontSize: '11px',
                      color: wearableConnected ? '#2E7D32' : '#C62828',
                      fontWeight: '700',
                      cursor: 'pointer'
                    }}
                  >
                    {wearableConnected ? '● Connected' : '○ Disconnected'}
                  </button>
                </div>

                <p style={{ fontSize: '10px', color: '#58696D', marginBottom: '12px' }}>
                  Source: <code>MockWearableAdapter.fetchLatestMetrics()</code> (Not live Bluetooth LE)
                </p>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '8px' }}>
                  <div style={{ backgroundColor: '#FAF9F6', padding: '10px', borderRadius: '12px', textAlign: 'center', border: '1px solid #E2ECE5' }}>
                    <div style={{ fontSize: '10px', color: '#58696D', fontWeight: '700' }}>Heart Rate</div>
                    <div style={{ fontSize: '18px', fontWeight: '800', color: '#1C3F30', marginTop: '2px' }}>{heartRate}</div>
                    <div style={{ fontSize: '9px', color: '#58696D' }}>bpm (Resting)</div>
                  </div>

                  <div style={{ backgroundColor: '#FAF9F6', padding: '10px', borderRadius: '12px', textAlign: 'center', border: '1px solid #E2ECE5' }}>
                    <div style={{ fontSize: '10px', color: '#58696D', fontWeight: '700' }}>Stress Score</div>
                    <div style={{ fontSize: '18px', fontWeight: '800', color: stressScore >= 60 ? '#C62828' : '#2E7D32', marginTop: '2px' }}>
                      {stressScore}/100
                    </div>
                    <div style={{ fontSize: '9px', color: '#58696D' }}>{stressScore >= 60 ? 'Elevated' : 'Balanced'}</div>
                  </div>

                  <div style={{ backgroundColor: '#FAF9F6', padding: '10px', borderRadius: '12px', textAlign: 'center', border: '1px solid #E2ECE5' }}>
                    <div style={{ fontSize: '10px', color: '#58696D', fontWeight: '700' }}>Steps</div>
                    <div style={{ fontSize: '18px', fontWeight: '800', color: '#1C3F30', marginTop: '2px' }}>{stepsCount}</div>
                    <div style={{ fontSize: '9px', color: '#58696D' }}>Today's Walk</div>
                  </div>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px', paddingTop: '10px', borderTop: '1px solid #F0F4F2', fontSize: '11px', color: '#58696D' }}>
                  <span>Sleep: <strong>{sleepDuration}</strong> (Score: {sleepScore})</span>
                  <button
                    onClick={() => setActiveModal('wearable_info')}
                    style={{ background: 'none', border: 'none', color: '#1C3F30', fontWeight: '700', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '2px' }}
                  >
                    <Info size={12} /> Tech Details
                  </button>
                </div>
              </div>

              {/* Saathi Companion Daily Anchor Card */}
              <div
                style={{
                  background: 'linear-gradient(135deg, #1C3F30 0%, #285A45 100%)',
                  borderRadius: '20px',
                  padding: '18px',
                  color: '#FAF9F6',
                  boxShadow: '0 8px 20px rgba(28, 63, 48, 0.25)'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div>
                    <span style={{ fontSize: '10px', letterSpacing: '0.8px', color: '#A3C2B0', fontWeight: '800' }}>
                      DAILY GROUNDING
                    </span>
                    <h3 style={{ fontSize: '16px', fontWeight: '800', marginTop: '2px' }}>
                      {checkInStress >= 7 ? 'One small breath at a time' : 'Ready for steady progress'}
                    </h3>
                  </div>
                  <SaathiAvatar name={saathiName} avatarId={saathiAvatar} size={38} mood={checkInStress >= 7 ? 'concerned' : 'encouraging'} />
                </div>

                <p style={{ fontSize: '12px', color: '#D6E8DE', marginTop: '8px', lineHeight: '18px' }}>
                  {checkInNote
                    ? `I noted: "${checkInNote}". You don't have to carry it alone today. We have a micro-step plan ready.`
                    : `Your mind is steady, and your schedule is aligned with your priorities. Focus on one task at a time.`}
                </p>

                <div style={{ display: 'flex', gap: '8px', marginTop: '14px' }}>
                  <button
                    onClick={() => {
                      setActiveModal('reset');
                      setIsResetRunning(true);
                    }}
                    style={{
                      flex: 1,
                      backgroundColor: '#FAF9F6',
                      color: '#1C3F30',
                      border: 'none',
                      borderRadius: '12px',
                      padding: '10px',
                      fontSize: '12px',
                      fontWeight: '800',
                      cursor: 'pointer',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      gap: '4px'
                    }}
                  >
                    <Heart size={14} color="#1C3F30" /> 2-Min Reset
                  </button>

                  <button
                    onClick={() => {
                      setCurrentTab('chat');
                    }}
                    style={{
                      flex: 1,
                      backgroundColor: 'rgba(255, 255, 255, 0.15)',
                      color: '#FAF9F6',
                      border: '1px solid rgba(255, 255, 255, 0.3)',
                      borderRadius: '12px',
                      padding: '10px',
                      fontSize: '12px',
                      fontWeight: '700',
                      cursor: 'pointer',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      gap: '4px'
                    }}
                  >
                    <MessageCircle size={14} /> Talk to Saathi
                  </button>
                </div>
              </div>

              {/* Today's Tasks Summary */}
              <div style={{ background: '#FFFFFF', border: '1px solid #E2ECE5', borderRadius: '20px', padding: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                  <h4 style={{ fontSize: '14px', fontWeight: '800', color: '#15222E' }}>Today's Action Plan</h4>
                  <button
                    onClick={() => setCurrentTab('tasks')}
                    style={{ background: 'none', border: 'none', fontSize: '11px', color: '#1C3F30', fontWeight: '700', cursor: 'pointer' }}
                  >
                    View All ({tasks.length}) ➔
                  </button>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {tasks.slice(0, 3).map((task) => (
                    <div
                      key={task.id}
                      onClick={() => toggleTask(task.id)}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '10px 12px',
                        backgroundColor: task.completed ? '#F5F5F5' : '#FAF9F6',
                        borderRadius: '12px',
                        border: '1px solid #E2ECE5',
                        cursor: 'pointer'
                      }}
                    >
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <div
                          style={{
                            width: '20px',
                            height: '20px',
                            borderRadius: '6px',
                            border: task.completed ? 'none' : '2px solid #58696D',
                            backgroundColor: task.completed ? '#2E7D32' : 'transparent',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center'
                          }}
                        >
                          {task.completed && <Check size={14} color="#FFF" />}
                        </div>
                        <span
                          style={{
                            fontSize: '12px',
                            fontWeight: '700',
                            color: task.completed ? '#888' : '#15222E',
                            textDecoration: task.completed ? 'line-through' : 'none'
                          }}
                        >
                          {task.title}
                        </span>
                      </div>
                      <span style={{ fontSize: '10px', color: '#58696D' }}>{task.time.split('–')[0]}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* ========================================================================= */}
          {/* SCREEN 6: MAIN APP - TAB: SAATHI CHAT */}
          {screen === 'main' && currentTab === 'chat' && (
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', height: '100%', backgroundColor: '#FAF9F6' }}>
              {/* Chat Header */}
              <div
                style={{
                  padding: '14px 16px',
                  backgroundColor: '#FFFFFF',
                  borderBottom: '1px solid #E2ECE5',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <SaathiAvatar name={saathiName} avatarId={saathiAvatar} size={42} mood={avatarMood} showGlow={true} />
                  <div>
                    <div style={{ fontSize: '14px', fontWeight: '800', color: '#1C3F30' }}>{saathiName}</div>
                    <div style={{ fontSize: '10px', color: '#58696D' }}>
                      {isAiThinking ? 'Saathi is thinking...' : 'Empathetic Companion • Always with you'}
                    </div>
                  </div>
                </div>

                <button
                  onClick={() => setActiveModal('safety')}
                  style={{
                    backgroundColor: '#FFEBEE',
                    border: '1px solid #FFCDD2',
                    borderRadius: '12px',
                    padding: '6px 10px',
                    fontSize: '11px',
                    fontWeight: '700',
                    color: '#C62828',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px'
                  }}
                >
                  <Shield size={12} /> Helplines
                </button>
              </div>

              {/* Stressor Test Prompts Quick-Bar */}
              <div
                style={{
                  backgroundColor: '#E8F2EC',
                  padding: '6px 12px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  overflowX: 'auto',
                  borderBottom: '1px solid #D6E8DE'
                }}
              >
                <span style={{ fontSize: '10px', fontWeight: '800', color: '#1C3F30', whiteSpace: 'nowrap' }}>
                  Test Prompts:
                </span>
                {[
                  'I am stressed because my assignment is pending.',
                  'My project is unfinished.',
                  'I have a fight with my friend.',
                  "I have too much work and don't know where to start."
                ].map((prompt, i) => (
                  <button
                    key={i}
                    onClick={() => handleSendMessage(prompt)}
                    style={{
                      backgroundColor: '#FFFFFF',
                      border: '1px solid #CBEAD7',
                      borderRadius: '10px',
                      padding: '3px 8px',
                      fontSize: '10px',
                      color: '#1C3F30',
                      fontWeight: '600',
                      whiteSpace: 'nowrap',
                      cursor: 'pointer'
                    }}
                  >
                    "{prompt.slice(0, 22)}..."
                  </button>
                ))}
              </div>

              {/* Chat Message Scroll */}
              <div style={{ flex: 1, overflowY: 'auto', padding: '16px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {messages.map((m) => (
                  <div
                    key={m.id}
                    style={{
                      alignSelf: m.sender === 'user' ? 'flex-end' : 'flex-start',
                      maxWidth: '85%',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '4px'
                    }}
                  >
                    <div
                      style={{
                        backgroundColor: m.sender === 'user' ? '#1C3F30' : '#FFFFFF',
                        color: m.sender === 'user' ? '#FFFFFF' : '#15222E',
                        padding: '12px 14px',
                        borderRadius: m.sender === 'user' ? '16px 16px 4px 16px' : '16px 16px 16px 4px',
                        border: m.sender === 'user' ? 'none' : '1px solid #E2ECE5',
                        fontSize: '13px',
                        lineHeight: '19px',
                        whiteSpace: 'pre-wrap',
                        boxShadow: '0 2px 6px rgba(0,0,0,0.04)'
                      }}
                    >
                      {m.text}
                    </div>

                    {/* Breakdown actionable card if present */}
                    {m.breakdown && (
                      <div
                        style={{
                          backgroundColor: '#FFF8E1',
                          border: '1px solid #FFE082',
                          borderRadius: '12px',
                          padding: '10px 12px',
                          marginTop: '6px'
                        }}
                      >
                        <div style={{ fontSize: '11px', fontWeight: '800', color: '#B78103' }}>
                          🎯 Identified Stressor: {m.breakdown.stressor}
                        </div>
                        <div style={{ fontSize: '11px', color: '#5D4037', margin: '4px 0' }}>
                          {m.breakdown.steps.length} micro-steps ready to execute.
                        </div>
                        <button
                          onClick={() => handleCreateTasksFromBreakdown(m.breakdown)}
                          style={{
                            backgroundColor: '#1C3F30',
                            color: '#FFF',
                            border: 'none',
                            borderRadius: '8px',
                            padding: '6px 10px',
                            fontSize: '11px',
                            fontWeight: '700',
                            cursor: 'pointer',
                            marginTop: '4px',
                            width: '100%'
                          }}
                        >
                          ➕ Add These 3 Micro-Steps to Today's Tasks
                        </button>
                      </div>
                    )}

                    {/* Suggestion Chips */}
                    {m.chips && m.chips.length > 0 && (
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '6px' }}>
                        {m.chips.map((chip, idx) => (
                          <button
                            key={idx}
                            onClick={() => {
                              if (chip.includes('2 Min Reset')) {
                                setActiveModal('reset');
                                setIsResetRunning(true);
                              } else if (chip.includes('Focus')) {
                                setActiveModal('focus');
                              } else if (chip.includes('Add')) {
                                if (m.breakdown) handleCreateTasksFromBreakdown(m.breakdown);
                              } else {
                                handleSendMessage(chip);
                              }
                            }}
                            style={{
                              backgroundColor: '#FFFFFF',
                              border: '1px solid #CBEAD7',
                              borderRadius: '12px',
                              padding: '5px 10px',
                              fontSize: '11px',
                              color: '#1C3F30',
                              fontWeight: '700',
                              cursor: 'pointer'
                            }}
                          >
                            {chip}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                ))}

                {isAiThinking && (
                  <div style={{ alignSelf: 'flex-start', display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 14px', backgroundColor: '#FFFFFF', borderRadius: '16px', border: '1px solid #E2ECE5' }}>
                    <SaathiAvatar name={saathiName} avatarId={saathiAvatar} size={24} mood="thinking" />
                    <span style={{ fontSize: '12px', color: '#58696D', fontStyle: 'italic' }}>
                      {saathiName} is decomposing your task with care...
                    </span>
                  </div>
                )}
              </div>

              {/* Chat Input Field */}
              <div style={{ padding: '12px 16px', backgroundColor: '#FFFFFF', borderTop: '1px solid #E2ECE5', display: 'flex', gap: '8px' }}>
                <input
                  type="text"
                  value={chatInput}
                  onChange={(e) => setChatInput(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
                  placeholder={`Share with ${saathiName}...`}
                  style={{
                    flex: 1,
                    padding: '12px 14px',
                    borderRadius: '14px',
                    border: '1px solid #E2ECE5',
                    fontSize: '13px',
                    outline: 'none',
                    backgroundColor: '#FAF9F6'
                  }}
                />
                <button
                  onClick={() => handleSendMessage()}
                  style={{
                    backgroundColor: '#1C3F30',
                    color: '#FAF9F6',
                    border: 'none',
                    borderRadius: '14px',
                    width: '44px',
                    height: '44px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    cursor: 'pointer'
                  }}
                >
                  <Send size={18} />
                </button>
              </div>
            </div>
          )}

          {/* ========================================================================= */}
          {/* SCREEN 6: MAIN APP - TAB: TASKS */}
          {screen === 'main' && currentTab === 'tasks' && (
            <div style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <h2 style={{ fontSize: '20px', fontWeight: '800', color: '#15222E' }}>Tasks & Pacing 📋</h2>
                  <p style={{ fontSize: '11px', color: '#58696D' }}>
                    {tasks.filter((t) => t.completed).length} of {tasks.length} completed today
                  </p>
                </div>
                <button
                  onClick={() => {
                    const title = prompt('Enter new task:');
                    if (title && title.trim()) {
                      const newTask = {
                        id: Date.now(),
                        title: title.trim(),
                        time: '11:00 AM – 12:00 PM',
                        category: 'Priority',
                        completed: false,
                        notes: 'Manual entry'
                      };
                      setTasks([...tasks, newTask]);
                    }
                  }}
                  style={{
                    backgroundColor: '#1C3F30',
                    color: '#FFF',
                    border: 'none',
                    borderRadius: '12px',
                    padding: '8px 12px',
                    fontSize: '12px',
                    fontWeight: '700',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px'
                  }}
                >
                  <Plus size={14} /> Add Task
                </button>
              </div>

              {/* Progress Bar */}
              <div style={{ backgroundColor: '#E2ECE5', height: '8px', borderRadius: '4px', overflow: 'hidden' }}>
                <div
                  style={{
                    backgroundColor: '#2E7D32',
                    height: '100%',
                    width: `${tasks.length > 0 ? (tasks.filter((t) => t.completed).length / tasks.length) * 100 : 0}%`,
                    transition: 'width 0.3s ease'
                  }}
                />
              </div>

              {/* Task List */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {tasks.map((task) => (
                  <div
                    key={task.id}
                    onClick={() => toggleTask(task.id)}
                    style={{
                      backgroundColor: task.completed ? '#F5F5F5' : '#FFFFFF',
                      border: '1px solid #E2ECE5',
                      borderRadius: '14px',
                      padding: '12px 14px',
                      display: 'flex',
                      alignItems: 'flex-start',
                      justifyContent: 'space-between',
                      cursor: 'pointer'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'flex-start', gap: '10px' }}>
                      <div
                        style={{
                          width: '20px',
                          height: '20px',
                          borderRadius: '6px',
                          border: task.completed ? 'none' : '2px solid #58696D',
                          backgroundColor: task.completed ? '#2E7D32' : 'transparent',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          marginTop: '2px'
                        }}
                      >
                        {task.completed && <Check size={14} color="#FFF" />}
                      </div>
                      <div>
                        <div
                          style={{
                            fontSize: '13px',
                            fontWeight: '700',
                            color: task.completed ? '#888' : '#15222E',
                            textDecoration: task.completed ? 'line-through' : 'none'
                          }}
                        >
                          {task.title}
                        </div>
                        <div style={{ fontSize: '11px', color: '#58696D', marginTop: '2px' }}>
                          {task.time} • {task.notes}
                        </div>
                      </div>
                    </div>
                    <span
                      style={{
                        fontSize: '10px',
                        backgroundColor: task.category === 'Wellness' ? '#E8F5E9' : '#E8F2EC',
                        color: task.category === 'Wellness' ? '#2E7D32' : '#1C3F30',
                        padding: '3px 8px',
                        borderRadius: '6px',
                        fontWeight: '700'
                      }}
                    >
                      {task.category}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* ========================================================================= */}
          {/* SCREEN 6: MAIN APP - TAB: CIRCLE */}
          {screen === 'main' && currentTab === 'circle' && (
            <div style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <h2 style={{ fontSize: '20px', fontWeight: '800', color: '#15222E' }}>Trusted Circle 🤝</h2>
                <p style={{ fontSize: '11px', color: '#58696D' }}>
                  Your support network for when you need to talk or ask for grounding.
                </p>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {contacts.map((contact) => (
                  <div
                    key={contact.id}
                    style={{
                      backgroundColor: '#FFFFFF',
                      border: '1px solid #E2ECE5',
                      borderRadius: '16px',
                      padding: '14px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                      <div
                        style={{
                          width: '42px',
                          height: '42px',
                          borderRadius: '50%',
                          backgroundColor: contact.color,
                          color: '#FFFFFF',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          fontWeight: '800',
                          fontSize: '16px'
                        }}
                      >
                        {contact.initial}
                      </div>
                      <div>
                        <div style={{ fontSize: '14px', fontWeight: '800', color: '#15222E' }}>{contact.name}</div>
                        <div style={{ fontSize: '11px', color: '#58696D' }}>{contact.relation}</div>
                      </div>
                    </div>

                    <div style={{ display: 'flex', gap: '6px' }}>
                      <button
                        onClick={() => {
                          setActiveContactForSupport(contact);
                          setActiveModal('support_request');
                        }}
                        style={{
                          backgroundColor: '#E8F2EC',
                          border: 'none',
                          borderRadius: '10px',
                          padding: '8px 12px',
                          fontSize: '11px',
                          fontWeight: '700',
                          color: '#1C3F30',
                          cursor: 'pointer'
                        }}
                      >
                        Send Support Ping
                      </button>
                      <a
                        href={`tel:${contact.phone}`}
                        style={{
                          backgroundColor: '#1C3F30',
                          color: '#FFF',
                          borderRadius: '10px',
                          padding: '8px',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          textDecoration: 'none'
                        }}
                      >
                        <Phone size={14} />
                      </a>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* ========================================================================= */}
          {/* SCREEN 6: MAIN APP - TAB: INSIGHTS */}
          {screen === 'main' && currentTab === 'insights' && (
            <div style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {/* Reusable Demo Mode Status Banner */}
              <DemoModeBanner
                onStartDemo={startDeterministicDemo}
                onOpenAuditModal={() => setActiveModal('wearable_info')}
              />

              <div>
                <h2 style={{ fontSize: '20px', fontWeight: '800', color: '#15222E' }}>Weekly Stability 📊</h2>
                <p style={{ fontSize: '11px', color: '#58696D' }}>
                  Emotional resilience and task pacing patterns over the last 7 days.
                </p>
              </div>

              {/* Stress & Mood Stability Chart */}
              <div style={{ backgroundColor: '#FFFFFF', border: '1px solid #E2ECE5', borderRadius: '18px', padding: '16px' }}>
                <div style={{ fontSize: '13px', fontWeight: '800', color: '#15222E', marginBottom: '10px' }}>
                  7-Day Mood & Stress Trend
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', height: '110px', padding: '10px 0' }}>
                  {[
                    { day: 'Mon', h: '60%', col: '#4CAF50', label: 'Good' },
                    { day: 'Tue', h: '80%', col: '#2E7D32', label: 'Great' },
                    { day: 'Wed', h: '45%', col: '#FFA000', label: 'Okay' },
                    { day: 'Thu', h: '70%', col: '#4CAF50', label: 'Good' },
                    { day: 'Fri', h: '35%', col: '#E53935', label: 'Hard' },
                    { day: 'Sat', h: '85%', col: '#2E7D32', label: 'Great' },
                    { day: 'Sun', h: `${checkInStress * 10}%`, col: checkInStress >= 7 ? '#E53935' : '#4CAF50', label: checkInMood }
                  ].map((bar, i) => (
                    <div key={i} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px', width: '28px' }}>
                      <div
                        style={{
                          width: '14px',
                          height: bar.h,
                          backgroundColor: bar.col,
                          borderRadius: '6px',
                          transition: 'height 0.3s ease'
                        }}
                      />
                      <span style={{ fontSize: '10px', color: '#58696D', fontWeight: '700' }}>{bar.day}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Private Journal Entries */}
              <div style={{ backgroundColor: '#FFFFFF', border: '1px solid #E2ECE5', borderRadius: '18px', padding: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <h4 style={{ fontSize: '13px', fontWeight: '800', color: '#15222E' }}>Private Journal</h4>
                  <button
                    onClick={() => setActiveModal('journal')}
                    style={{ background: 'none', border: 'none', color: '#1C3F30', fontSize: '11px', fontWeight: '700', cursor: 'pointer' }}
                  >
                    + Write Entry
                  </button>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {journalEntries.map((entry) => (
                    <div key={entry.id} style={{ backgroundColor: '#FAF9F6', border: '1px solid #E2ECE5', borderRadius: '10px', padding: '8px 10px' }}>
                      <div style={{ fontSize: '10px', color: '#58696D', fontWeight: '700' }}>{entry.date}</div>
                      <div style={{ fontSize: '12px', color: '#15222E', marginTop: '2px' }}>{entry.text}</div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Bottom Navigation Bar */}
        {screen === 'main' && (
          <div className="bottom-nav">
            <button className={`nav-item ${currentTab === 'home' ? 'active' : ''}`} onClick={() => setCurrentTab('home')}>
              <Home size={18} />
              <span className="label">Home</span>
            </button>
            <button className={`nav-item ${currentTab === 'chat' ? 'active' : ''}`} onClick={() => setCurrentTab('chat')}>
              <MessageCircle size={18} />
              <span className="label">Saathi AI</span>
            </button>
            <button className={`nav-item ${currentTab === 'tasks' ? 'active' : ''}`} onClick={() => setCurrentTab('tasks')}>
              <CheckSquare size={18} />
              <span className="label">Tasks</span>
            </button>
            <button className={`nav-item ${currentTab === 'circle' ? 'active' : ''}`} onClick={() => setCurrentTab('circle')}>
              <Users size={18} />
              <span className="label">Circle</span>
            </button>
            <button className={`nav-item ${currentTab === 'insights' ? 'active' : ''}`} onClick={() => setCurrentTab('insights')}>
              <BarChart2 size={18} />
              <span className="label">Insights</span>
            </button>
          </div>
        )}

        {/* ========================================================================= */}
        {/* MODAL 1: 2-MINUTE CALMING RESET (4-7-8 METHOD) */}
        {activeModal === 'reset' && (
          <div
            style={{
              position: 'absolute',
              inset: 0,
              backgroundColor: 'rgba(14, 24, 19, 0.95)',
              zIndex: 120,
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
              padding: '28px 24px',
              color: '#FAF9F6'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ fontSize: '12px', fontWeight: '800', color: '#A3C2B0' }}>
                4-7-8 CALMING NERVOUS SYSTEM RESET
              </span>
              <button
                onClick={() => {
                  setActiveModal(null);
                  setIsResetRunning(false);
                }}
                style={{ background: 'none', border: 'none', color: '#FAF9F6', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}>
              <div
                style={{
                  width: '180px',
                  height: '180px',
                  borderRadius: '50%',
                  background:
                    resetPhase === 'Inhale'
                      ? 'radial-gradient(circle, #2E7D32 0%, #1C3F30 100%)'
                      : resetPhase === 'Hold'
                      ? 'radial-gradient(circle, #1565C0 0%, #0D47A1 100%)'
                      : 'radial-gradient(circle, #4CAF50 0%, #2E7D32 100%)',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  justifyContent: 'center',
                  boxShadow: '0 0 35px rgba(46, 125, 50, 0.5)',
                  transform: resetPhase === 'Inhale' ? 'scale(1.2)' : resetPhase === 'Hold' ? 'scale(1.2)' : 'scale(0.9)',
                  transition: 'all 3s ease'
                }}
              >
                <span style={{ fontSize: '20px', fontWeight: '800' }}>{resetPhase}</span>
                <span style={{ fontSize: '32px', fontWeight: '900', marginTop: '4px' }}>{resetSeconds}s</span>
              </div>

              <div style={{ marginTop: '24px', fontSize: '13px', color: '#D6E8DE' }}>
                Cycle {resetCycle} of 4 • Signals your brain that you are safe.
              </div>
            </div>

            <div style={{ display: 'flex', gap: '8px' }}>
              <button
                onClick={() => setIsResetRunning(!isResetRunning)}
                style={{
                  flex: 1,
                  backgroundColor: isResetRunning ? '#D32F2F' : '#2E7D32',
                  color: '#FAF9F6',
                  border: 'none',
                  borderRadius: '14px',
                  padding: '14px',
                  fontSize: '13px',
                  fontWeight: '800',
                  cursor: 'pointer'
                }}
              >
                {isResetRunning ? 'Pause Breathing' : 'Resume Breathing'}
              </button>
              <button
                onClick={() => {
                  setActiveModal(null);
                  setIsResetRunning(false);
                  setShowToast('2-Minute Reset Complete 🌿');
                  setTimeout(() => setShowToast(null), 3000);
                }}
                style={{
                  flex: 1,
                  backgroundColor: '#FFFFFF',
                  color: '#1C3F30',
                  border: 'none',
                  borderRadius: '14px',
                  padding: '14px',
                  fontSize: '13px',
                  fontWeight: '800',
                  cursor: 'pointer'
                }}
              >
                Finish & Feel Grounded
              </button>
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* MODAL 2: 25-MINUTE FOCUS SESSION */}
        {activeModal === 'focus' && (
          <div
            style={{
              position: 'absolute',
              inset: 0,
              backgroundColor: '#1C3F30',
              zIndex: 120,
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
              padding: '28px 24px',
              color: '#FAF9F6'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ fontSize: '12px', fontWeight: '800', color: '#A3C2B0' }}>DEEP FOCUS BLOCK</span>
              <button
                onClick={() => {
                  setActiveModal(null);
                  setIsFocusRunning(false);
                }}
                style={{ background: 'none', border: 'none', color: '#FAF9F6', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}>
              <SaathiAvatar name={saathiName} avatarId={saathiAvatar} size={64} mood="listening" />

              <div style={{ fontSize: '16px', fontWeight: '800', marginTop: '16px', color: '#FFF' }}>
                {focusTask}
              </div>
              <div style={{ fontSize: '11px', color: '#D6E8DE', marginTop: '4px' }}>
                One single action. Notifications silenced.
              </div>

              <div style={{ fontSize: '48px', fontWeight: '900', color: '#FFF', margin: '24px 0' }}>
                {String(Math.floor(focusSecondsLeft / 60)).padStart(2, '0')}:
                {String(focusSecondsLeft % 60).padStart(2, '0')}
              </div>
            </div>

            <div style={{ display: 'flex', gap: '8px' }}>
              <button
                onClick={() => setIsFocusRunning(!isFocusRunning)}
                style={{
                  flex: 1,
                  backgroundColor: isFocusRunning ? '#D32F2F' : '#2E7D32',
                  color: '#FAF9F6',
                  border: 'none',
                  borderRadius: '14px',
                  padding: '14px',
                  fontSize: '13px',
                  fontWeight: '800',
                  cursor: 'pointer'
                }}
              >
                {isFocusRunning ? 'Pause Timer' : 'Start Focus'}
              </button>
              <button
                onClick={() => {
                  setActiveModal(null);
                  setIsFocusRunning(false);
                  setShowToast('Focus Block Complete! Great work 🎯');
                  setTimeout(() => setShowToast(null), 3000);
                }}
                style={{
                  flex: 1,
                  backgroundColor: '#FFFFFF',
                  color: '#1C3F30',
                  border: 'none',
                  borderRadius: '14px',
                  padding: '14px',
                  fontSize: '13px',
                  fontWeight: '800',
                  cursor: 'pointer'
                }}
              >
                Complete Sprint
              </button>
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* MODAL 3: SAFETY & VERIFIED 24/7 HELPLINES */}
        {activeModal === 'safety' && (
          <div
            style={{
              position: 'absolute',
              inset: 0,
              backgroundColor: 'rgba(0, 0, 0, 0.75)',
              zIndex: 130,
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'flex-end'
            }}
          >
            <div
              style={{
                backgroundColor: '#FFFFFF',
                borderRadius: '24px 24px 0 0',
                padding: '24px',
                maxHeight: '85vh',
                overflowY: 'auto',
                display: 'flex',
                flexDirection: 'column',
                gap: '12px'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Shield size={20} color="#C62828" />
                  <h3 style={{ fontSize: '18px', fontWeight: '800', color: '#15222E' }}>Support & Safety Flow</h3>
                </div>
                <button onClick={() => setActiveModal(null)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                  <X size={20} color="#58696D" />
                </button>
              </div>

              {/* Strict Medical Disclaimer */}
              <div
                style={{
                  backgroundColor: '#FFEBEE',
                  border: '1px solid #FFCDD2',
                  borderRadius: '12px',
                  padding: '10px 12px',
                  fontSize: '11px',
                  color: '#C62828',
                  lineHeight: '16px'
                }}
              >
                <strong>Clinical Safety Notice:</strong> SAATH is an emotional wellbeing companion. It does NOT diagnose psychiatric disorders, prescribe medication, or substitute for emergency psychological services. Wearable metrics measure autonomic stress response, not mental illness.
              </div>

              <div style={{ fontSize: '12px', fontWeight: '700', color: '#15222E', marginTop: '6px' }}>
                Verified Free 24/7 National Helplines:
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {EMERGENCY_RESOURCES.map((res, idx) => (
                  <div
                    key={idx}
                    style={{
                      backgroundColor: '#FAF9F6',
                      border: '1px solid #E2ECE5',
                      borderRadius: '12px',
                      padding: '10px 12px',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center'
                    }}
                  >
                    <div>
                      <div style={{ fontSize: '13px', fontWeight: '800', color: '#15222E' }}>{res.name}</div>
                      <div style={{ fontSize: '11px', color: '#58696D' }}>{res.desc}</div>
                    </div>
                    <a
                      href={res.callUrl}
                      style={{
                        backgroundColor: '#C62828',
                        color: '#FFF',
                        borderRadius: '10px',
                        padding: '8px 12px',
                        fontSize: '12px',
                        fontWeight: '800',
                        textDecoration: 'none',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px'
                      }}
                    >
                      <Phone size={12} /> {res.number}
                    </a>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* MODAL 4: SUPPORT REQUEST (SEND TO TRUSTED CIRCLE) */}
        {activeModal === 'support_request' && activeContactForSupport && (
          <div
            style={{
              position: 'absolute',
              inset: 0,
              backgroundColor: 'rgba(0, 0, 0, 0.75)',
              zIndex: 130,
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'flex-end'
            }}
          >
            <div
              style={{
                backgroundColor: '#FFFFFF',
                borderRadius: '24px 24px 0 0',
                padding: '24px',
                display: 'flex',
                flexDirection: 'column',
                gap: '12px'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ fontSize: '16px', fontWeight: '800', color: '#15222E' }}>
                  Reach Out to {activeContactForSupport.name}
                </h3>
                <button onClick={() => setActiveModal(null)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                  <X size={20} color="#58696D" />
                </button>
              </div>

              <p style={{ fontSize: '12px', color: '#58696D' }}>
                Drafted Grounding Message (tap to send via SMS / WhatsApp):
              </p>

              <div style={{ backgroundColor: '#FAF9F6', border: '1px solid #E2ECE5', borderRadius: '12px', padding: '12px', fontSize: '12px', color: '#15222E' }}>
                "Hey {activeContactForSupport.name}, I'm feeling a bit overwhelmed with my assignments right now and could use a quick chat or grounding conversation whenever you're free today. 💚"
              </div>

              <div style={{ display: 'flex', gap: '8px', marginTop: '6px' }}>
                <a
                  href={`tel:${activeContactForSupport.phone}`}
                  style={{
                    flex: 1,
                    backgroundColor: '#1C3F30',
                    color: '#FFF',
                    borderRadius: '12px',
                    padding: '12px',
                    textAlign: 'center',
                    textDecoration: 'none',
                    fontWeight: '800',
                    fontSize: '13px'
                  }}
                >
                  Call Directly
                </a>
                <button
                  onClick={() => {
                    setActiveModal(null);
                    setShowToast(`Support request sent to ${activeContactForSupport.name} 💌`);
                    setTimeout(() => setShowToast(null), 3000);
                  }}
                  style={{
                    flex: 1,
                    backgroundColor: '#E8F2EC',
                    color: '#1C3F30',
                    border: '1px solid #CBEAD7',
                    borderRadius: '12px',
                    padding: '12px',
                    fontWeight: '800',
                    fontSize: '13px',
                    cursor: 'pointer'
                  }}
                >
                  Send Ping
                </button>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* MODAL 5: WEARABLE AUDIT INFO */}
        {activeModal === 'wearable_info' && (
          <div
            style={{
              position: 'absolute',
              inset: 0,
              backgroundColor: 'rgba(0, 0, 0, 0.75)',
              zIndex: 130,
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'flex-end'
            }}
          >
            <div
              style={{
                backgroundColor: '#FFFFFF',
                borderRadius: '24px 24px 0 0',
                padding: '24px',
                display: 'flex',
                flexDirection: 'column',
                gap: '12px'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ fontSize: '16px', fontWeight: '800', color: '#15222E' }}>
                  Wearable Architecture Audit
                </h3>
                <button onClick={() => setActiveModal(null)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                  <X size={20} color="#58696D" />
                </button>
              </div>

              <div style={{ fontSize: '12px', color: '#58696D', lineHeight: '18px' }}>
                <strong>Data Source Status:</strong> DEMO / MOCK ROOM DATA
                <br />
                <strong>Implementation:</strong> <code>MockWearableAdapter.fetchLatestMetrics()</code>
                <br />
                <strong>Production Bridge:</strong> <code>AmazfitAdapter (Zepp Bridge BLE)</code>
                <br />
                <br />
                Live Amazfit Band 7 telemetry requires Bluetooth LE GATT profiles connected via the Zepp Open Platform. In Demo Mode, metrics are safely simulated to demonstrate reality engine pacing.
              </div>

              <button
                onClick={() => setActiveModal(null)}
                style={{
                  backgroundColor: '#1C3F30',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '12px',
                  padding: '12px',
                  fontWeight: '800',
                  fontSize: '13px',
                  cursor: 'pointer'
                }}
              >
                Understood
              </button>
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* MODAL 6: PRIVATE JOURNAL COMPOSER */}
        {activeModal === 'journal' && (
          <div
            style={{
              position: 'absolute',
              inset: 0,
              backgroundColor: 'rgba(0, 0, 0, 0.75)',
              zIndex: 130,
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'flex-end'
            }}
          >
            <div
              style={{
                backgroundColor: '#FFFFFF',
                borderRadius: '24px 24px 0 0',
                padding: '24px',
                display: 'flex',
                flexDirection: 'column',
                gap: '12px'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ fontSize: '16px', fontWeight: '800', color: '#15222E' }}>Private Journal Entry</h3>
                <button onClick={() => setActiveModal(null)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                  <X size={20} color="#58696D" />
                </button>
              </div>

              <textarea
                rows={4}
                value={journalInput}
                onChange={(e) => setJournalInput(e.target.value)}
                placeholder="Unload what's on your mind. Stored only on your device..."
                style={{
                  padding: '12px',
                  borderRadius: '12px',
                  border: '1px solid #E2ECE5',
                  fontSize: '13px',
                  fontFamily: 'inherit',
                  outline: 'none'
                }}
              />

              <button
                onClick={() => {
                  handleSaveJournal();
                  setActiveModal(null);
                }}
                style={{
                  backgroundColor: '#1C3F30',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '12px',
                  padding: '12px',
                  fontWeight: '800',
                  fontSize: '13px',
                  cursor: 'pointer'
                }}
              >
                Save Private Note 🔒
              </button>
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* MODAL 7: SETTINGS & NUDGE CONFIGURATION */}
        {activeModal === 'settings' && (
          <div
            style={{
              position: 'absolute',
              inset: 0,
              backgroundColor: 'rgba(0, 0, 0, 0.75)',
              zIndex: 130,
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'flex-end'
            }}
          >
            <div
              style={{
                backgroundColor: '#FFFFFF',
                borderRadius: '24px 24px 0 0',
                padding: '24px',
                maxHeight: '85vh',
                overflowY: 'auto',
                display: 'flex',
                flexDirection: 'column',
                gap: '14px'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ fontSize: '18px', fontWeight: '800', color: '#15222E' }}>Settings & Notifications</h3>
                <button onClick={() => setActiveModal(null)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                  <X size={20} color="#58696D" />
                </button>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {/* Notification Toggle */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <div style={{ fontSize: '13px', fontWeight: '700', color: '#15222E' }}>Smart Nudges</div>
                    <div style={{ fontSize: '11px', color: '#58696D' }}>Morning check-ins and focus reminders</div>
                  </div>
                  <input
                    type="checkbox"
                    checked={nudgeSettings.enabled}
                    onChange={(e) => {
                      const updated = { ...nudgeSettings, enabled: e.target.checked };
                      setNudgeSettings(updated);
                      NudgeManager.saveSettings(updated);
                    }}
                    style={{ accentColor: '#1C3F30', width: '18px', height: '18px' }}
                  />
                </div>

                {/* Quiet Hours */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <div style={{ fontSize: '13px', fontWeight: '700', color: '#15222E' }}>Quiet Hours</div>
                    <div style={{ fontSize: '11px', color: '#58696D' }}>10:00 PM – 07:00 AM (No nudges sent)</div>
                  </div>
                  <input
                    type="checkbox"
                    checked={nudgeSettings.quietHoursEnabled}
                    onChange={(e) => {
                      const updated = { ...nudgeSettings, quietHoursEnabled: e.target.checked };
                      setNudgeSettings(updated);
                      NudgeManager.saveSettings(updated);
                    }}
                    style={{ accentColor: '#1C3F30', width: '18px', height: '18px' }}
                  />
                </div>

                {/* Daily Cap */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <div style={{ fontSize: '13px', fontWeight: '700', color: '#15222E' }}>Maximum Daily Nudges</div>
                    <div style={{ fontSize: '11px', color: '#58696D' }}>Prevents notification fatigue</div>
                  </div>
                  <span style={{ fontSize: '12px', fontWeight: '800', color: '#1C3F30' }}>
                    {nudgeSettings.maxDailyNudges} / day
                  </span>
                </div>

                {/* Reset Data Button */}
                <button
                  onClick={() => {
                    localStorage.removeItem('saath_tasks_data');
                    localStorage.removeItem('saath_checkin');
                    localStorage.removeItem('saath_journal');
                    setTasks(INITIAL_TASKS);
                    setActiveModal(null);
                    setShowToast('App reset to initial demo state ↺');
                    setTimeout(() => setShowToast(null), 3000);
                  }}
                  style={{
                    backgroundColor: '#FFEBEE',
                    color: '#C62828',
                    border: '1px solid #FFCDD2',
                    borderRadius: '12px',
                    padding: '12px',
                    fontSize: '12px',
                    fontWeight: '800',
                    cursor: 'pointer',
                    marginTop: '10px'
                  }}
                >
                  Reset Local Storage Demo Data
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
