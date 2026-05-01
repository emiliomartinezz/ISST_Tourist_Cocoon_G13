/**
 * background-paths.jsx
 * Adapted from 21st.dev "Background Paths" component
 *
 * Changes from original:
 *  - Removed "use client" (Next.js only — Vite/React doesn't need it)
 *  - Converted TypeScript → JavaScript (project uses JSX, tsx: false)
 *  - Colors adapted to Tourist Cocoon forest-green palette
 *  - Background transparent so the existing nature gradient shows through
 *  - FloatingPaths exported separately for use as an overlay
 *  - Button styled with forest-green theme instead of dark/white
 *
 * Dependencies: framer-motion (npm install framer-motion)
 */

import { motion } from "framer-motion";
import { Button } from "@/Components/ui/button";

/**
 * Renders 36 animated SVG paths that flow across the screen.
 * position: 1 = left-to-right sweep, -1 = right-to-left sweep (use both for symmetry)
 */
export function FloatingPaths({ position }) {
  const paths = Array.from({ length: 36 }, (_, i) => ({
    id: i,
    d: `M-${380 - i * 5 * position} -${189 + i * 6}C-${
      380 - i * 5 * position
    } -${189 + i * 6} -${312 - i * 5 * position} ${216 - i * 6} ${
      152 - i * 5 * position
    } ${343 - i * 6}C${616 - i * 5 * position} ${470 - i * 6} ${
      684 - i * 5 * position
    } ${875 - i * 6} ${684 - i * 5 * position} ${875 - i * 6}`,
    // Forest-green palette: deeper lines for later paths
    color: `rgba(31, 58, 45, ${0.06 + i * 0.022})`,
    width: 0.5 + i * 0.03,
  }));

  return (
    <div className="absolute inset-0 pointer-events-none overflow-hidden">
      <svg
        className="w-full h-full"
        viewBox="0 0 696 316"
        fill="none"
        aria-hidden="true"
      >
        {paths.map((path) => (
          <motion.path
            key={path.id}
            d={path.d}
            stroke={path.color}
            strokeWidth={path.width}
            strokeOpacity={0.06 + path.id * 0.022}
            initial={{ pathLength: 0.3, opacity: 0.6 }}
            animate={{
              pathLength: 1,
              opacity: [0.3, 0.6, 0.3],
              pathOffset: [0, 1, 0],
            }}
            transition={{
              duration: 20 + Math.random() * 10,
              repeat: Number.POSITIVE_INFINITY,
              ease: "linear",
            }}
          />
        ))}
      </svg>
    </div>
  );
}

/**
 * Full-page hero component with animated background paths + animated title.
 * Can be used standalone as a landing/welcome page.
 *
 * Props:
 *   title      – string split into words, each letter animates in separately
 *   subtitle   – optional subtitle below the title
 *   ctaLabel   – CTA button label (falsy = hide button)
 *   onCtaClick – callback for the CTA button
 */
export function BackgroundPaths({
  title = "Tourist Cocoon",
  subtitle = "Tu refugio en la naturaleza",
  ctaLabel = "Descubrir",
  onCtaClick,
}) {
  const words = title.split(" ");

  return (
    <div className="relative min-h-screen w-full flex items-center justify-center overflow-hidden bg-white/0">
      {/* Two mirrored path layers for depth */}
      <div className="absolute inset-0">
        <FloatingPaths position={1} />
        <FloatingPaths position={-1} />
      </div>

      <div className="relative z-10 container mx-auto px-4 md:px-6 text-center">
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 2 }}
          className="max-w-4xl mx-auto"
        >
          {/* Animated letter-by-letter title */}
          <h1 className="font-bold mb-6 tracking-tighter"
            style={{ fontSize: "clamp(2.8rem, 8vw, 6rem)", lineHeight: 1.05 }}
          >
            {words.map((word, wordIndex) => (
              <span key={wordIndex} className="inline-block mr-4 last:mr-0">
                {word.split("").map((letter, letterIndex) => (
                  <motion.span
                    key={`${wordIndex}-${letterIndex}`}
                    initial={{ y: 80, opacity: 0 }}
                    animate={{ y: 0, opacity: 1 }}
                    transition={{
                      delay: wordIndex * 0.12 + letterIndex * 0.035,
                      type: "spring",
                      stiffness: 150,
                      damping: 25,
                    }}
                    className="inline-block text-transparent bg-clip-text"
                    style={{
                      backgroundImage:
                        "linear-gradient(140deg, var(--forest-900) 0%, var(--forest-500) 100%)",
                    }}
                  >
                    {letter}
                  </motion.span>
                ))}
              </span>
            ))}
          </h1>

          {/* Subtitle */}
          {subtitle && (
            <motion.p
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.8, duration: 0.6 }}
              className="mb-8 text-base md:text-lg font-medium"
              style={{ color: "var(--muted-foreground)" }}
            >
              {subtitle}
            </motion.p>
          )}

          {/* CTA button — glass pill with gradient border (21st.dev inspired) */}
          {ctaLabel && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 1.1, duration: 0.5 }}
              className="inline-block group relative p-px rounded-2xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300"
              style={{
                background:
                  "linear-gradient(135deg, rgba(79,127,102,0.4), rgba(168,207,176,0.6), rgba(79,127,102,0.4))",
              }}
            >
              <Button
                variant="ghost"
                onClick={onCtaClick}
                className="rounded-[0.9rem] px-8 py-5 text-base font-semibold backdrop-blur-md transition-all duration-300 group-hover:-translate-y-0.5"
                style={{
                  background: "rgba(255,255,255,0.90)",
                  color: "var(--forest-900)",
                  border: "none",
                }}
              >
                <span className="opacity-90 group-hover:opacity-100 transition-opacity">
                  {ctaLabel}
                </span>
                <span className="ml-3 opacity-70 group-hover:opacity-100 group-hover:translate-x-1.5 transition-all duration-300 inline-block">
                  →
                </span>
              </Button>
            </motion.div>
          )}
        </motion.div>
      </div>
    </div>
  );
}
