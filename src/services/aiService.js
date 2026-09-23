// SAATH Intelligent Companion & Reality Engine Service
// Adheres strictly to non-diagnostic wellbeing companion architecture

const HIGH_RISK_PATTERNS = [
  'suicide', 'suicidal', 'kill myself', 'killing myself', 'end my life', 'ending my life',
  'want to die', "don't want to live", 'dont want to live', 'harm myself', 'harming myself',
  'hurt myself', 'hurting myself', 'cutting', 'overdose', 'end it all', 'cannot go on',
  "can't go on", 'cant go on', 'no reason to live', 'better off dead'
];

export const EMERGENCY_RESOURCES = [
  {
    name: 'Tele-MANAS (Govt of India)',
    desc: 'Free 24x7 Mental Health Helpline',
    number: '14416',
    callUrl: 'tel:14416'
  },
  {
    name: 'KIRAN Helpline',
    desc: 'National 24/7 Mental Health Assistance',
    number: '1800-599-0019',
    callUrl: 'tel:18005990019'
  },
  {
    name: 'Vandrevala Foundation',
    desc: '24/7 Free Crisis Counseling',
    number: '9999 666 555',
    callUrl: 'tel:9999666555'
  },
  {
    name: 'AASRA',
    desc: 'Suicide Prevention & Emotional Crisis',
    number: '+91 98204 66726',
    callUrl: 'tel:+919820466726'
  },
  {
    name: 'Emergency Services',
    desc: 'Immediate Ambulance & Police Support',
    number: '112',
    callUrl: 'tel:112'
  }
];

// Safety Engine: zero-latency crisis screening
export function checkSafety(text) {
  const lower = (text || '').toLowerCase();
  for (const pattern of HIGH_RISK_PATTERNS) {
    if (lower.includes(pattern)) {
      return {
        isHighRisk: true,
        level: 'HIGH_RISK',
        message: 'I hear you, and you are not alone right now. Please reach out immediately to a trusted person or one of these free, 24/7 confidential helplines. People care and support is available.',
        resources: EMERGENCY_RESOURCES
      };
    }
  }
  return { isHighRisk: false, level: 'EVERYDAY_SUPPORT' };
}

// Reality Engine: Context-aware micro-task decomposition
export function runRealityEngine(inputText, userName = 'Friend') {
  const lower = (inputText || '').toLowerCase();

  let category = 'Priority Task';
  let stressorName = 'Task Overwhelm';
  let taskTitle = 'Core Priority Sprint';
  let steps = [];
  let resetType = '2-Minute Box Breathing';

  if (lower.includes('project') || lower.includes('unfinished') || lower.includes('code') || lower.includes('app')) {
    stressorName = 'Project Scope & Completion';
    taskTitle = 'Unfinished Project Milestone';
    steps = [
      'Write down the single blocker stopping this project from running (5 mins)',
      'Strip out non-essential extras and define a minimum working version (10 mins)',
      'Run one 25-minute deep focus sprint on just that core module'
    ];
    resetType = '2-Minute Shoulder & Neck Stretch';
  } else if (lower.includes('fight') || lower.includes('friend') || lower.includes('argument') || lower.includes('relationship')) {
    stressorName = 'Interpersonal Friction & Emotional Processing';
    taskTitle = 'Emotional Grounding & Space';
    steps = [
      'Do a 2-minute 4-7-8 breathing reset to calm the nervous system',
      'Unload your raw thoughts in the private SAATH Journal without sending anything (10 mins)',
      'Allow 24 hours of cool-down time before deciding on a calm conversation'
    ];
    resetType = '2-Minute 4-7-8 Heart Calming';
  } else if (lower.includes('too much work') || lower.includes('where to start') || lower.includes('overload') || lower.includes('paralyzed')) {
    stressorName = 'Cognitive Overwhelm & Decision Paralysis';
    taskTitle = 'Micro-Action Momentum';
    steps = [
      'Brain dump all pending items onto a blank sheet without organizing (3 mins)',
      'Pick the single easiest item that takes under 5 minutes to create momentum',
      'Hide all other tasks from view and complete that one micro-step'
    ];
    resetType = '2-Minute Grounding 5-4-3-2-1 Sensory Scan';
  } else if (lower.includes('assignment') || lower.includes('exam') || lower.includes('study') || lower.includes('homework')) {
    stressorName = 'Academic Deadline Pressure';
    taskTitle = 'Assignment Breakdown Sprint';
    steps = [
      'Open the document and write 3 simple outline bullet points (5 mins)',
      'Gather main source references without judging output quality (10 mins)',
      'Start a 25-minute uninterrupted Pomodoro sprint on Section 1'
    ];
    resetType = '2-Minute Box Breathing Reset';
  } else if (lower.includes('tired') || lower.includes('exhausted') || lower.includes('sleep') || lower.includes('burnout')) {
    stressorName = 'Physical & Mental Depletion';
    taskTitle = 'Gentle Recovery Protocol';
    steps = [
      'Drink a full glass of cool water and rest your eyes (3 mins)',
      'Postpone 2 non-critical items to tomorrow without self-judgment',
      'Schedule a light 15-minute walk or horizontal resting pause'
    ];
    resetType = '2-Minute Mindful Body Scan';
  } else {
    stressorName = 'Day-to-day Task Pressure';
    taskTitle = 'Single Focus Block';
    steps = [
      'Take 3 slow grounding breaths with an extended exhale',
      'Select just ONE action you can finish in 15 minutes',
      'Mark it done in SAATH and celebrate showing up'
    ];
    resetType = '2-Minute Centering Breath';
  }

  return {
    category,
    stressorName,
    taskTitle,
    steps,
    resetType,
    reply: `I understand, ${userName}. When stress spikes, our brains see a mountain instead of steps. Let's make it manageable right now. 🌿\n\nI identified this as **${stressorName}**.\n\nHere are 3 micro-actions:\n${steps.map((s, i) => `${i + 1}. ${s}`).join('\n')}\n\nWould you like to do a quick 2-minute reset, or start a 25-minute focus session?`
  };
}

// Call Gemini API securely with timeout and heuristic fallback
export async function generateSaathiReply({
  userMessage,
  userName = 'Friend',
  saathiName = 'Aarav',
  checkIn = null,
  tasks = []
}) {
  // 1. First line of defense: Safety Engine
  const safety = checkSafety(userMessage);
  if (safety.isHighRisk) {
    return {
      text: safety.message,
      safetyLevel: 'HIGH_RISK',
      emergencyResources: safety.resources,
      actionType: 'EMERGENCY_SUPPORT',
      avatarMood: 'concerned'
    };
  }

  const lower = (userMessage || '').toLowerCase();

  // 2. Reality Engine check for stressors, assignments, conflicts, or overwhelm
  const hasStressorKeyword = 
    lower.includes('assignment') || lower.includes('project') || lower.includes('fight') ||
    lower.includes('work') || lower.includes('stress') || lower.includes('overwhelm') ||
    lower.includes('where to start') || lower.includes('tired') || lower.includes('exhausted') ||
    lower.includes('stuck') || lower.includes('too much');

  if (hasStressorKeyword) {
    const breakdown = runRealityEngine(userMessage, userName);
    return {
      text: breakdown.reply,
      safetyLevel: 'EVERYDAY_SUPPORT',
      breakdown: {
        stressor: breakdown.stressorName,
        taskTitle: breakdown.taskTitle,
        steps: breakdown.steps
      },
      actionType: 'TASK_BREAKDOWN',
      avatarMood: 'encouraging',
      suggestionChips: ['2 Min Reset', '25 Min Focus', 'Add 3 Tasks to Today', 'Talk more']
    };
  }

  // 3. Check for Gemini API Key in Environment
  const apiKey = (typeof process !== 'undefined' && process.env && process.env.GEMINI_API_KEY) || '';

  if (apiKey && apiKey !== 'MY_GEMINI_API_KEY') {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 7500);

      const systemPrompt = `You are ${saathiName}, a warm, grounded 24/7 personal wellbeing companion in the SAATH app for ${userName}.
Guidelines:
- Tone: calm, empathetic, conversational, brief (2-3 sentences max).
- NEVER diagnose medical or mental health conditions.
- NEVER prescribe medication.
- If wearable metrics are mentioned, treat them as general physiological indications (e.g. elevated heart rate or stress score), NOT clinical diagnoses.
- Guide the user toward small practical next steps, breathing pauses, or celebrating small wins.`;

      const contextPrompt = `User said: "${userMessage}".
Current context: User name=${userName}, Mood=${checkIn?.mood || 'Good'}, Stress=${checkIn?.stressLevel || 4}/10, Active tasks=${tasks.filter(t => !t.isCompleted).length}.`;

      const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=${apiKey}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        signal: controller.signal,
        body: JSON.stringify({
          contents: [{ parts: [{ text: contextPrompt }] }],
          systemInstruction: { parts: [{ text: systemPrompt }] },
          generationConfig: {
            temperature: 0.7,
            maxOutputTokens: 1024
          }
        })
      });

      clearTimeout(timeoutId);

      if (res.ok) {
        const data = await res.json();
        const text = data?.candidates?.[0]?.content?.parts?.[0]?.text;
        if (text && typeof text === 'string' && text.trim().length > 0) {
          return {
            text: text.trim(),
            safetyLevel: 'EVERYDAY_SUPPORT',
            avatarMood: 'speaking',
            suggestionChips: ['2 Min Reset', 'Show Today\'s Plan', 'How am I doing?', 'I feel better']
          };
        }
      }
    } catch (err) {
      console.warn('Gemini API fetch fallback triggered:', err.message);
    }
  }

  // 4. Empathetic Fallback Heuristic
  let fallbackText = `I'm listening, ${userName}. Whatever is on your plate today, remember you don't have to carry it all at once. What's the smallest step we can take right now?`;
  let avatarMood = 'listening';

  if (lower.includes('hello') || lower.includes('hi') || lower.includes('hey')) {
    fallbackText = `Hey ${userName}! 🌿 I'm right here with you. How are you feeling right now? We can review today's plan, do a calming breath, or just chat.`;
    avatarMood = 'speaking';
  } else if (lower.includes('thank')) {
    fallbackText = `Always in your corner, ${userName}. Every small step counts. You're doing better than you give yourself credit for! ✨`;
    avatarMood = 'encouraging';
  }

  return {
    text: fallbackText,
    safetyLevel: 'EVERYDAY_SUPPORT',
    avatarMood,
    suggestionChips: ['2 Min Reset', 'Show Today\'s Plan', 'Add a Task', 'Take a Breath']
  };
}
