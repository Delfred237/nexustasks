export function LandingFooter() {
  return (
    <footer className="border-t py-12">
      <div className="container mx-auto px-4">
        <div className="flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-2">
            <img
              src="/app_icon.png"
              alt="NexusTasks logo"
              className="h-8 w-8 rounded-lg"
            />
            <span className="font-bold">NexusTasks</span>
          </div>

          <nav className="flex items-center gap-6 text-sm text-muted-foreground">
            <a
              href="#features"
              className="hover:text-foreground transition-colors"
            >
              Features
            </a>
            <a
              href="#how-it-works"
              className="hover:text-foreground transition-colors"
            >
              How it works
            </a>
            <a
              href="#pricing"
              className="hover:text-foreground transition-colors"
            >
              Pricing
            </a>
          </nav>

          <p className="text-sm text-muted-foreground">
            © 2026 NexusTasks. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
}
