import React from 'react';
import { Leaf, Heart, Sparkles, Volume2, Mic, AlertCircle } from 'lucide-react';

export default function SaathiAvatar({
  name = 'Aarav',
  avatarId = 'aarav',
  size = 56,
  mood = 'idle', // 'idle' | 'listening' | 'thinking' | 'speaking' | 'encouraging' | 'concerned'
  showGlow = true
}) {
  const getAvatarColors = () => {
    switch (avatarId.toLowerCase()) {
      case 'mira':
        return {
          bg: 'linear-gradient(135deg, #FFD1DC 0%, #FDE2E4 100%)',
          letter: 'M',
          accent: '#E56B6F',
          glow: 'rgba(229, 107, 111, 0.25)'
        };
      case 'kian':
        return {
          bg: 'linear-gradient(135deg, #D0E1FD 0%, #E2EAFC 100%)',
          letter: 'K',
          accent: '#4F7CFF',
          glow: 'rgba(79, 124, 255, 0.25)'
        };
      case 'nova':
        return {
          bg: 'linear-gradient(135deg, #FDE2B8 0%, #FFF1DC 100%)',
          letter: 'N',
          accent: '#E6A23C',
          glow: 'rgba(230, 162, 60, 0.25)'
        };
      default: // Aarav
        return {
          bg: 'linear-gradient(135deg, #CBEAD7 0%, #E8F6ED 100%)',
          letter: (name && name[0]) || 'A',
          accent: '#1C3F30',
          glow: 'rgba(28, 63, 48, 0.3)'
        };
    }
  };

  const colors = getAvatarColors();

  // Mood specific badge and halo styling
  const getMoodConfig = () => {
    switch (mood) {
      case 'listening':
        return {
          badgeIcon: <Mic size={Math.round(size * 0.22)} color="#1C3F30" />,
          badgeBg: '#E8F2EC',
          borderCol: '#1C3F30',
          animationClass: 'pulse-listening',
          statusText: 'Listening...'
        };
      case 'thinking':
        return {
          badgeIcon: <Sparkles size={Math.round(size * 0.22)} color="#1565C0" />,
          badgeBg: '#E3F2FD',
          borderCol: '#1565C0',
          animationClass: 'pulse-thinking',
          statusText: 'Thinking...'
        };
      case 'speaking':
        return {
          badgeIcon: <Volume2 size={Math.round(size * 0.22)} color="#2E7D32" />,
          badgeBg: '#E8F5E9',
          borderCol: '#2E7D32',
          animationClass: 'pulse-speaking',
          statusText: 'Speaking...'
        };
      case 'encouraging':
        return {
          badgeIcon: <Sparkles size={Math.round(size * 0.22)} color="#E65100" />,
          badgeBg: '#FFF3E0',
          borderCol: '#E65100',
          animationClass: 'pulse-encouraging',
          statusText: 'Encouraging'
        };
      case 'concerned':
        return {
          badgeIcon: <Heart size={Math.round(size * 0.22)} color="#C62828" fill="#C62828" />,
          badgeBg: '#FFEBEE',
          borderCol: '#D32F2F',
          animationClass: 'pulse-concerned',
          statusText: 'Caring & Grounding'
        };
      default: // idle
        return {
          badgeIcon: <Leaf size={Math.round(size * 0.22)} color="#1C3F30" />,
          badgeBg: '#FFFFFF',
          borderCol: colors.accent,
          animationClass: 'pulse-idle',
          statusText: 'Always with you'
        };
    }
  };

  const moodConfig = getMoodConfig();

  return (
    <div
      style={{
        position: 'relative',
        width: `${size}px`,
        height: `${size}px`,
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        flexShrink: 0
      }}
      title={`${name} • ${moodConfig.statusText}`}
    >
      {/* Outer Glow Halo */}
      {showGlow && (
        <div
          style={{
            position: 'absolute',
            inset: -4,
            borderRadius: '50%',
            background: colors.glow,
            zIndex: 1,
            pointerEvents: 'none'
          }}
          className={moodConfig.animationClass}
        />
      )}

      {/* Main Avatar Bubble */}
      <div
        style={{
          width: '100%',
          height: '100%',
          borderRadius: '50%',
          background: colors.bg,
          border: `2.5px solid ${moodConfig.borderCol}`,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          fontWeight: '800',
          fontSize: `${Math.round(size * 0.44)}px`,
          color: colors.accent,
          position: 'relative',
          zIndex: 2,
          boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
          transition: 'all 0.3s ease'
        }}
      >
        <span>{colors.letter}</span>

        {/* Small Mood Badge */}
        <div
          style={{
            position: 'absolute',
            bottom: -2,
            right: -2,
            width: `${Math.round(size * 0.36)}px`,
            height: `${Math.round(size * 0.36)}px`,
            borderRadius: '50%',
            backgroundColor: moodConfig.badgeBg,
            border: '1.5px solid #FFFFFF',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 2px 4px rgba(0,0,0,0.15)',
            zIndex: 3
          }}
        >
          {moodConfig.badgeIcon}
        </div>
      </div>
    </div>
  );
}
