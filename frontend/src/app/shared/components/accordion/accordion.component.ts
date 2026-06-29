import { Component, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideChevronDown } from '@lucide/angular';

export interface AccordionItem {
  title: string;
  content: string;
}

@Component({
  selector: 'app-accordion',
  standalone: true,
  imports: [CommonModule, LucideChevronDown],
  templateUrl: './accordion.component.html',
  styleUrls: ['./accordion.component.scss']
})
export class AccordionComponent {
  items = input.required<AccordionItem[]>();

  // Track open state of each item by index
  openIndex = signal<number | null>(null);

  toggle(index: number) {
    this.openIndex.update(current => current === index ? null : index);
  }
}
