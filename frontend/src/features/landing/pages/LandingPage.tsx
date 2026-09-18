import { LandingHeader } from "../components/LandingHeader";
import { Hero } from "../components/Hero";
import { Features } from "../components/Features";
import { HowItWorks } from "../components/HowItWorks";
import { LandingCTA } from "../components/LandingCTA";
import { LandingFooter } from "../components/LandingFooter";

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-background">
      <LandingHeader />
      <main>
        <Hero />
        <Features />
        <HowItWorks />
        <LandingCTA />
      </main>
      <LandingFooter />
    </div>
  );
}
